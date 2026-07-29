package com.poc.odmauth.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

// Simule odm-authentication (document §7.1) :
// reçoit un id_token Keycloak, le valide via JWKS, émet un JWT interne HS512
// avec le même secret partagé que integration-hub (mode shared-jwt).
@RestController
@RequestMapping("/odm-authentication")
public class OidcTokenController {

    private final JwtDecoder keycloakJwtDecoder;
    private final SecretKey internalSigningKey;

    public OidcTokenController(JwtDecoder keycloakJwtDecoder,
                                @Value("${hub.jwt.secret}") String base64InternalSecret) {
        this.keycloakJwtDecoder = keycloakJwtDecoder;
        this.internalSigningKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64InternalSecret));
    }

    @PostMapping("/oidcToken")
    public ResponseEntity<Map<String, String>> exchangeToken(@RequestBody Map<String, String> body) {
        String idToken = body.get("id_token");
        if (idToken == null || idToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "id_token requis"));
        }

        Jwt keycloakJwt;
        try {
            keycloakJwt = keycloakJwtDecoder.decode(idToken);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "id_token invalide: " + e.getMessage()));
        }

        String subject = keycloakJwt.getSubject();
        String tenantId = keycloakJwt.getClaimAsString("tenant_id");
        if (tenantId == null) {
            tenantId = "UNKNOWN";
        }

        String internalJwt = Jwts.builder()
                .subject(subject)
                .claim("tenantId", tenantId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(internalSigningKey)
                .compact();

        return ResponseEntity.ok(Map.of("token", internalJwt));
    }
}

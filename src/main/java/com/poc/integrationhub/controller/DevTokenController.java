package com.poc.integrationhub.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

// ATTENTION : endpoint de développement uniquement, à supprimer avant tout usage réel.
// Sert à générer un token de test signé avec le même secret que le Hub (simule odm-authentication).
@RestController
@RequestMapping("/router-api/2/_dev")
public class DevTokenController {

    private final SecretKey signingKey;

    public DevTokenController(@Value("${hub.jwt.secret}") String base64Secret) {
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
    }

    @PostMapping("/generate-token")
    public Map<String, String> generateToken(@RequestParam(defaultValue = "GEMY") String tenantId,
                                               @RequestParam(defaultValue = "test-user") String subject) {
        String token = Jwts.builder()
                .subject(subject)
                .claim("tenantId", tenantId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000)) // 1h
                .signWith(signingKey)
                .compact();

        return Map.of("token", token);
    }
}

package com.poc.integrationhub.controller;

import com.poc.integrationhub.service.CryptoService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/router-api/2/_debug")
public class DebugCryptoController {

    private final CryptoService cryptoService;

    public DebugCryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @PostMapping("/crypto-test")
    public Map<String, String> test(@RequestBody Map<String, String> body) {
        String plain = body.get("value");
        String encrypted = cryptoService.encrypt(plain);
        String decrypted = cryptoService.decrypt(encrypted);
        return Map.of("original", plain, "encrypted", encrypted, "decrypted", decrypted);
    }
}
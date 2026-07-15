package com.poc.integrationhub.controller;

import com.poc.integrationhub.entity.ConnectorConfig;
import com.poc.integrationhub.repository.ConnectorConfigRepository;
import com.poc.integrationhub.service.CryptoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/router-api/2/connectors/{connectorId}/config")
public class ConnectorConfigController {

    private final ConnectorConfigRepository repository;
    private final CryptoService cryptoService;

    public ConnectorConfigController(ConnectorConfigRepository repository, CryptoService cryptoService) {
        this.repository = repository;
        this.cryptoService = cryptoService;
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> upsert(@PathVariable String connectorId,
                                                         @RequestBody Map<String, Object> body) {
        String configKey = (String) body.get("configKey");
        boolean isSecret = Boolean.TRUE.equals(body.get("isSecret"));
        String rawValue = (String) body.get("configValue");
        String storedValue = isSecret ? cryptoService.encrypt(rawValue) : rawValue;

        ConnectorConfig config = repository.findByConnectorId(connectorId).stream()
                .filter(c -> c.getConfigKey().equals(configKey))
                .findFirst()
                .orElseGet(ConnectorConfig::new);

        config.setConnectorId(connectorId);
        config.setConfigKey(configKey);
        config.setConfigValue(storedValue);
        config.setSecret(isSecret);
        repository.save(config);

        return ResponseEntity.ok(Map.of("configKey", configKey, "isSecret", isSecret, "status", "SAVED"));
    }

    @GetMapping
    public List<Map<String, Object>> list(@PathVariable String connectorId) {
        return repository.findByConnectorId(connectorId).stream()
                .map(c -> Map.<String, Object>of(
                        "configKey", c.getConfigKey(),
                        "configValue", c.isSecret() ? "***MASKED***" : c.getConfigValue(),
                        "isSecret", c.isSecret()
                ))
                .toList();
    }
}
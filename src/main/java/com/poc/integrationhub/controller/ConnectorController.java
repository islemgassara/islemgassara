package com.poc.integrationhub.controller;

import com.poc.integrationhub.entity.Connector;
import com.poc.integrationhub.repository.ConnectorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Map;

@RestController
@RequestMapping("/router-api/2/connectors")
public class ConnectorController {

    private final ConnectorRepository connectorRepository;

    public ConnectorController(ConnectorRepository connectorRepository) {
        this.connectorRepository = connectorRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Connector connector) {
        try {
            Connector saved = connectorRepository.save(connector);
            return ResponseEntity.ok(saved);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body(Map.of(
                    "error", "CONNECTOR_ALREADY_EXISTS",
                    "message", "Un connecteur avec l'id '" + connector.getId() + "' existe déjà. Utilisez PUT pour le mettre à jour."
            ));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Connector> update(@PathVariable String id, @RequestBody Connector connector) {
        return connectorRepository.findById(id)
                .map(existing -> {
                    existing.setTenantId(connector.getTenantId());
                    existing.setN8nWebhookPath(connector.getN8nWebhookPath());
                    existing.setActive(connector.isActive());
                    return ResponseEntity.ok(connectorRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
}

    @GetMapping("/{id}")
    public ResponseEntity<Connector> getById(@PathVariable String id) {
        return connectorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
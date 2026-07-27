package com.poc.integrationhub.controller;

import com.poc.integrationhub.entity.MappingRule;
import com.poc.integrationhub.mapping.MappingService;
import com.poc.integrationhub.repository.MappingRuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/router-api/2/connectors/{connectorId}")
public class MappingController {

    private final MappingRuleRepository mappingRuleRepository;
    private final MappingService mappingService;

    public MappingController(MappingRuleRepository mappingRuleRepository, MappingService mappingService) {
        this.mappingRuleRepository = mappingRuleRepository;
        this.mappingService = mappingService;
    }

    @GetMapping("/mappings")
    public ResponseEntity<List<MappingRule>> listMappings(@PathVariable String connectorId) {
        return ResponseEntity.ok(mappingRuleRepository.findByConnectorId(connectorId));
    }

    @PostMapping("/mappings")
    public ResponseEntity<MappingRule> addMapping(@PathVariable String connectorId,
                                                    @RequestBody MappingRule rule) {
        rule.setConnectorId(connectorId);
        MappingRule saved = mappingRuleRepository.save(rule);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/mappings/{id}")
    public ResponseEntity<Void> deleteMapping(@PathVariable String connectorId, @PathVariable Long id) {
        mappingRuleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/mappings/preview")
    public ResponseEntity<Map<String, Object>> preview(@PathVariable String connectorId,
                                                          @RequestBody Map<String, Object> payload) {
        Map<String, Object> mapped = mappingService.applyMapping(connectorId, payload);
        return ResponseEntity.ok(mapped);
    }
}
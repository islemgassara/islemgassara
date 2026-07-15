package com.poc.integrationhub.controller;

import com.poc.integrationhub.service.ExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/router-api/2/connectors/{connectorId}")
public class ExecutionController {

    private final ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> execute(@PathVariable String connectorId,
                                                          @RequestBody Map<String, Object> payload) {
        Map<String, Object> result = executionService.execute(connectorId, payload);
        return ResponseEntity.ok(result);
    }
}

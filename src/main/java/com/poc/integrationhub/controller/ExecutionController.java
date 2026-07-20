package com.poc.integrationhub.controller;

import com.poc.integrationhub.entity.Connector;
import com.poc.integrationhub.exception.ConnectorNotFoundException;
import com.poc.integrationhub.repository.ConnectorRepository;
import com.poc.integrationhub.service.ExecutionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/router-api/2/connectors/{connectorId}")
public class ExecutionController {

    private final ExecutionService executionService;
    private final ConnectorRepository connectorRepository;

    public ExecutionController(ExecutionService executionService, ConnectorRepository connectorRepository) {
        this.executionService = executionService;
        this.connectorRepository = connectorRepository;
    }

    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> execute(@PathVariable String connectorId,
                                                          @RequestBody Map<String, Object> payload,
                                                          HttpServletRequest request) {
        String tokenTenantId = (String) request.getAttribute("tenantId");

        Connector connector = connectorRepository.findById(connectorId)
                .orElseThrow(() -> new ConnectorNotFoundException(connectorId));

        if (tokenTenantId != null && !tokenTenantId.equals(connector.getTenantId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "TENANT_MISMATCH",
                            "message", "Le tenant du token ne correspond pas au tenant du connecteur"));
        }

        Map<String, Object> result = executionService.execute(connectorId, payload);
        return ResponseEntity.ok(result);
    }
}

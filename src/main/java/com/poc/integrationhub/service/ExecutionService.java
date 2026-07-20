package com.poc.integrationhub.service;

import com.poc.integrationhub.entity.Connector;
import com.poc.integrationhub.entity.ExecutionLog;
import com.poc.integrationhub.exception.ConnectorInactiveException;
import com.poc.integrationhub.exception.ConnectorNotFoundException;
import com.poc.integrationhub.exception.N8nCallFailedException;
import com.poc.integrationhub.mapping.MappingService;
import com.poc.integrationhub.repository.ConnectorRepository;
import com.poc.integrationhub.repository.ExecutionLogRepository;
import com.poc.integrationhub.repository.ConnectorConfigRepository;
import com.poc.integrationhub.entity.ConnectorConfig;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExecutionService {

    private final ConnectorRepository connectorRepository;
    private final MappingService mappingService;
    private final N8nGatewayService n8nGatewayService;
    private final ExecutionLogRepository executionLogRepository;
    private final ConnectorConfigRepository connectorConfigRepository;
    private final CryptoService cryptoService;

    public ExecutionService(ConnectorRepository connectorRepository,
                             MappingService mappingService,
                             N8nGatewayService n8nGatewayService,
                             ExecutionLogRepository executionLogRepository,
                             ConnectorConfigRepository connectorConfigRepository,
                             CryptoService cryptoService) {
        this.connectorRepository = connectorRepository;
        this.mappingService = mappingService;
        this.n8nGatewayService = n8nGatewayService;
        this.executionLogRepository = executionLogRepository;
        this.connectorConfigRepository = connectorConfigRepository;
        this.cryptoService = cryptoService;
    }

    public Map<String, Object> execute(String connectorId, Map<String, Object> requestPayload) {
        ExecutionLog log = new ExecutionLog();
        log.setConnectorId(connectorId);
        log.setRequestPayload(requestPayload);

        Connector connector = connectorRepository.findById(connectorId)
                .orElseThrow(() -> new ConnectorNotFoundException(connectorId));

        if (!connector.isActive()) {
            log.setTenantId(connector.getTenantId());
            log.setStatus("CONNECTOR_INACTIVE");
            executionLogRepository.save(log);
            throw new ConnectorInactiveException(connectorId);
        }
        log.setTenantId(connector.getTenantId());

        Map<String, Object> mappedPayload = mappingService.applyMapping(connectorId, requestPayload);
        log.setMappedPayload(mappedPayload);
        log.setStatus("IN_PROGRESS");
        executionLogRepository.save(log);

        try {
            List<ConnectorConfig> configs = connectorConfigRepository.findByConnectorId(connectorId);
            Map<String, Object> configMap = new HashMap<>();
            for (ConnectorConfig cfg : configs) {
                String value = cfg.isSecret() ? cryptoService.decrypt(cfg.getConfigValue()) : cfg.getConfigValue();
                configMap.put(cfg.getConfigKey(), value);
            }

            String executionId = log.getId() != null ? log.getId().toString() : "unknown";

            // Structure conforme au document (ligne 164-169) :
            // body = { executionId, mappedPayload: {...}, config: {...} }
            Map<String, Object> n8nBody = new HashMap<>();
            n8nBody.put("executionId", executionId);
            n8nBody.put("mappedPayload", mappedPayload);
            n8nBody.put("config", configMap);

            Map<String, Object> response = n8nGatewayService.callWebhook(
                    connector.getN8nWebhookPath(), n8nBody, connector.getTenantId(), executionId
            );
            log.setResponsePayload(response);
            log.setStatus("SUCCESS");
            executionLogRepository.save(log);
            return response;
        } catch (Exception e) {
            log.setStatus("N8N_FAILED");
            executionLogRepository.save(log);
            throw new N8nCallFailedException(e.getMessage(), e);
        }
    }
}

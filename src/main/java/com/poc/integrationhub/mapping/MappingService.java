package com.poc.integrationhub.mapping;

import com.poc.integrationhub.entity.MappingRule;
import com.poc.integrationhub.repository.MappingRuleRepository;
import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MappingService {

    private final MappingRuleRepository mappingRuleRepository;
    private final JsonLogic jsonLogic = new JsonLogic();

    public MappingService(MappingRuleRepository mappingRuleRepository) {
        this.mappingRuleRepository = mappingRuleRepository;
    }

    public Map<String, Object> applyMapping(String connectorId, Map<String, Object> inputPayload) {
        List<MappingRule> rules = mappingRuleRepository.findByConnectorId(connectorId);
        Map<String, Object> output = new HashMap<>();

        for (MappingRule rule : rules) {
            Object value = resolveValue(rule.getSourceExpr(), inputPayload);
            output.put(rule.getTargetField(), value);
        }
        return output;
    }

    private Object resolveValue(String sourceExpr, Map<String, Object> payload) {
        if (sourceExpr.startsWith("STATIC:")) {
            return sourceExpr.substring("STATIC:".length());
        }
        if (sourceExpr.trim().startsWith("{")) {
            try {
                return jsonLogic.apply(sourceExpr, payload);
            } catch (JsonLogicException e) {
                throw new RuntimeException("Erreur JsonLogic sur l'expression: " + sourceExpr, e);
            }
        }
        return resolveDotPath(sourceExpr, payload);
    }

    @SuppressWarnings("unchecked")
    private Object resolveDotPath(String path, Map<String, Object> payload) {
        String[] parts = path.split("\\.");
        Object current = payload;
        for (String part : parts) {
            if (!(current instanceof Map)) return null;
            current = ((Map<String, Object>) current).get(part);
        }
        return current;
    }
}
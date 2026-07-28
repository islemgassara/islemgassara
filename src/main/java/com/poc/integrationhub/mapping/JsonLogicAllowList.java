package com.poc.integrationhub.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component
public class JsonLogicAllowList {

    private static final Set<String> ALLOWED_OPERATORS = Set.of(
            "var", "missing", "missing_some",
            "if", "==", "===", "!=", "!==", "!", "!!",
            "and", "or",
            ">", ">=", "<", "<=",
            "+", "-", "*", "/", "%", "max", "min",
            "in", "cat", "substr", "merge",
            "map", "filter", "reduce", "all", "none", "some"
    );

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void validate(String jsonLogicExpr) {
        JsonNode root;
        try {
            root = objectMapper.readTree(jsonLogicExpr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Expression JsonLogic invalide (JSON malformé): " + jsonLogicExpr, e);
        }
        walk(root, jsonLogicExpr);
    }

    private void walk(JsonNode node, String originalExpr) {
        if (node == null || !node.isObject()) {
            if (node != null && node.isArray()) {
                for (JsonNode child : node) {
                    walk(child, originalExpr);
                }
            }
            return;
        }

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String operator = entry.getKey();

            if (!ALLOWED_OPERATORS.contains(operator)) {
                throw new SecurityException(
                        "Opérateur JsonLogic non autorisé: '" + operator +
                        "' dans l'expression: " + originalExpr +
                        ". Opérateurs autorisés: " + ALLOWED_OPERATORS
                );
            }

            walk(entry.getValue(), originalExpr);
        }
    }
}

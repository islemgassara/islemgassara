package com.poc.integrationhub.mapping;

import com.poc.integrationhub.entity.MappingRule;
import com.poc.integrationhub.repository.MappingRuleRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MappingServiceTest {

    @Test
    void appliesDotPathMapping() {
        MappingRuleRepository repo = mock(MappingRuleRepository.class);
        MappingRule rule = new MappingRule();
        rule.setTargetField("nom_client");
        rule.setSourceExpr("client.fullName");
        when(repo.findByConnectorId("cfn")).thenReturn(List.of(rule));

        MappingService service = new MappingService(repo);
        Map<String, Object> input = Map.of("client", Map.of("fullName", "Jean Dupont"));

        Map<String, Object> result = service.applyMapping("cfn", input);

        assertEquals("Jean Dupont", result.get("nom_client"));
    }

    @Test
    void appliesStaticValueMapping() {
        MappingRuleRepository repo = mock(MappingRuleRepository.class);
        MappingRule rule = new MappingRule();
        rule.setTargetField("pays");
        rule.setSourceExpr("STATIC:FR");
        when(repo.findByConnectorId("cfn")).thenReturn(List.of(rule));

        MappingService service = new MappingService(repo);
        Map<String, Object> result = service.applyMapping("cfn", Map.of());

        assertEquals("FR", result.get("pays"));
    }
}

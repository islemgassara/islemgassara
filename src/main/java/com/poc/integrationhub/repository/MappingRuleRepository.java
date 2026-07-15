package com.poc.integrationhub.repository;

import com.poc.integrationhub.entity.MappingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MappingRuleRepository extends JpaRepository<MappingRule, Long> {
    List<MappingRule> findByConnectorIdAndTenantIdAndContextType(String connectorId, String tenantId, String contextType);
    List<MappingRule> findByConnectorId(String connectorId);
}


package com.poc.integrationhub.repository;

import com.poc.integrationhub.entity.ConnectorConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConnectorConfigRepository extends JpaRepository<ConnectorConfig, Long> {
    List<ConnectorConfig> findByConnectorId(String connectorId);
}
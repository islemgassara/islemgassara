package com.poc.integrationhub.repository;

import com.poc.integrationhub.entity.Connector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectorRepository extends JpaRepository<Connector, String> {
}
package com.poc.integrationhub.repository;

import com.poc.integrationhub.entity.ExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ExecutionLogRepository extends JpaRepository<ExecutionLog, UUID> {
}
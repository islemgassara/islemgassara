package com.poc.integrationhub.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "execution_log")
public class ExecutionLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "connector_id", nullable = false)
    private String connectorId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_payload", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> requestPayload;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "mapped_payload", columnDefinition = "jsonb")
    private Map<String, Object> mappedPayload;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_payload", columnDefinition = "jsonb")
    private Map<String, Object> responsePayload;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @PrePersist
    protected void onCreate() {
        executedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public String getConnectorId() { return connectorId; }
    public void setConnectorId(String connectorId) { this.connectorId = connectorId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Map<String, Object> getRequestPayload() { return requestPayload; }
    public void setRequestPayload(Map<String, Object> requestPayload) { this.requestPayload = requestPayload; }
    public Map<String, Object> getMappedPayload() { return mappedPayload; }
    public void setMappedPayload(Map<String, Object> mappedPayload) { this.mappedPayload = mappedPayload; }
    public Map<String, Object> getResponsePayload() { return responsePayload; }
    public void setResponsePayload(Map<String, Object> responsePayload) { this.responsePayload = responsePayload; }
    public LocalDateTime getExecutedAt() { return executedAt; }
}
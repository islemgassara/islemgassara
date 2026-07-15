package com.poc.integrationhub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mapping_rule")
public class MappingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "connector_id", nullable = false)
    private String connectorId;

    @Column(name = "target_field", nullable = false)
    private String targetField;

    @Column(name = "source_expr", nullable = false)
    private String sourceExpr;

    private String transform;

    @Column(nullable = false)
    private boolean required = false;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "context_type")
    private String contextType;

    public Long getId() { return id; }
    public String getConnectorId() { return connectorId; }
    public void setConnectorId(String connectorId) { this.connectorId = connectorId; }
    public String getTargetField() { return targetField; }
    public void setTargetField(String targetField) { this.targetField = targetField; }
    public String getSourceExpr() { return sourceExpr; }
    public void setSourceExpr(String sourceExpr) { this.sourceExpr = sourceExpr; }
    public String getTransform() { return transform; }
    public void setTransform(String transform) { this.transform = transform; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getContextType() { return contextType; }
    public void setContextType(String contextType) { this.contextType = contextType; }
}
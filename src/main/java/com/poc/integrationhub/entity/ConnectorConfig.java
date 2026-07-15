package com.poc.integrationhub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "connector_config")
public class ConnectorConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "connector_id", nullable = false)
    private String connectorId;

    @Column(name = "config_key", nullable = false)
    private String configKey;

    @Column(name = "config_value", nullable = false)
    private String configValue;

    @Column(name = "is_secret", nullable = false)
    private boolean isSecret = false;

    public Long getId() { return id; }
    public String getConnectorId() { return connectorId; }
    public void setConnectorId(String connectorId) { this.connectorId = connectorId; }
    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
    public boolean isSecret() { return isSecret; }
    public void setSecret(boolean secret) { isSecret = secret; }
}
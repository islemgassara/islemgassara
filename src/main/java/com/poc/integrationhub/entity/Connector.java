package com.poc.integrationhub.entity;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;
import java.time.LocalDateTime;

@Entity
@Table(name = "connector")
public class Connector implements Persistable<String> {

    @Id
    private String id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "n8n_webhook_path", nullable = false)
    private String n8nWebhookPath;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private boolean isNew = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }

    @Override
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getN8nWebhookPath() { return n8nWebhookPath; }
    public void setN8nWebhookPath(String n8nWebhookPath) { this.n8nWebhookPath = n8nWebhookPath; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean isNew() { return isNew; }
}
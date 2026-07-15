CREATE TABLE connector (
    id            VARCHAR(50) PRIMARY KEY,
    tenant_id     VARCHAR(50) NOT NULL,
    n8n_webhook_path VARCHAR(255) NOT NULL,
    active        BOOLEAN NOT NULL DEFAULT true,
    created_at    TIMESTAMP NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP NOT NULL DEFAULT now()
);
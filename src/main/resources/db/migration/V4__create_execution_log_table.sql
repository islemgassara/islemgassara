CREATE TABLE execution_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    connector_id    VARCHAR(50) NOT NULL REFERENCES connector(id),
    tenant_id       VARCHAR(50) NOT NULL,
    status          VARCHAR(30) NOT NULL,
    request_payload JSONB NOT NULL,
    mapped_payload  JSONB,
    response_payload JSONB,
    executed_at     TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_execution_tenant ON execution_log(tenant_id, executed_at);
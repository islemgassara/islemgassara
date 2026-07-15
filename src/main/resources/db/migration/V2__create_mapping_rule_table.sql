CREATE TABLE mapping_rule (
    id              BIGSERIAL PRIMARY KEY,
    connector_id    VARCHAR(50) NOT NULL REFERENCES connector(id),
    target_field    VARCHAR(255) NOT NULL,
    source_expr     TEXT NOT NULL,
    transform       VARCHAR(50),
    required        BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX idx_mapping_lookup ON mapping_rule(connector_id);
CREATE TABLE connector_config (
    id              BIGSERIAL PRIMARY KEY,
    connector_id    VARCHAR(50) NOT NULL REFERENCES connector(id),
    config_key      VARCHAR(100) NOT NULL,
    config_value    TEXT NOT NULL,
    is_secret       BOOLEAN NOT NULL DEFAULT false,

    UNIQUE (connector_id, config_key)
);
ALTER TABLE mapping_rule ADD COLUMN tenant_id VARCHAR(50);
ALTER TABLE mapping_rule ADD COLUMN context_type VARCHAR(50);

DROP INDEX IF EXISTS idx_mapping_lookup;
CREATE INDEX idx_mapping_lookup ON mapping_rule(connector_id, tenant_id, context_type);

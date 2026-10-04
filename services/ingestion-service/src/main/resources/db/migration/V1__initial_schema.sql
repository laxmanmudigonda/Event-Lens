CREATE TABLE projects (
  id UUID PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE,
  created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE api_keys (
  id UUID PRIMARY KEY,
  project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
  name VARCHAR(100) NOT NULL,
  key_prefix VARCHAR(16) NOT NULL,
  key_hash VARCHAR(64) NOT NULL UNIQUE,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE alert_rules (
  id UUID PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  service VARCHAR(100) NOT NULL,
  level VARCHAR(16) NOT NULL,
  threshold_count INTEGER NOT NULL CHECK (threshold_count > 0),
  window_seconds INTEGER NOT NULL CHECK (window_seconds BETWEEN 60 AND 86400),
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE alert_history (
  id UUID PRIMARY KEY,
  rule_id UUID NOT NULL REFERENCES alert_rules(id) ON DELETE CASCADE,
  observed_count BIGINT NOT NULL,
  triggered_at TIMESTAMPTZ NOT NULL
);

INSERT INTO projects(id, name, created_at)
VALUES ('00000000-0000-0000-0000-000000000001', 'Local Demo', CURRENT_TIMESTAMP);
INSERT INTO api_keys(id, project_id, name, key_prefix, key_hash, enabled, created_at)
VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001',
        'development', 'evl_dev_key', '9f78b839676d801add4608ba2f8f5da8a0f3815f9e4d908d948d7df4c086b257', TRUE, CURRENT_TIMESTAMP);


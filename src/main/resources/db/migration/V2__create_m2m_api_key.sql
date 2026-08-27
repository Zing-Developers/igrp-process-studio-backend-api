-- Machine-to-machine API keys (docs/SPEC_M2M_AUTHORIZATION.md in the management API repo).
-- Only the HMAC-SHA-256 of the key is stored; the plaintext is shown once at creation.
-- V2 on purpose: baseline-on-migrate stamps existing schemas at version 1, so this runs both on
-- baselined and on fresh databases. IF NOT EXISTS keeps it idempotent where dev ddl-auto created it.
CREATE TABLE IF NOT EXISTS t_m2m_api_key (
    id            UUID PRIMARY KEY,
    client_name   TEXT        NOT NULL,
    key_prefix    TEXT        NOT NULL,
    key_hash      TEXT        NOT NULL,
    permissions   TEXT        NOT NULL,
    email         TEXT,
    active        BOOLEAN     NOT NULL DEFAULT TRUE,
    expires_at    TIMESTAMPTZ,
    created_by    TEXT        NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_used_at  TIMESTAMPTZ,
    revoked_at    TIMESTAMPTZ,
    CONSTRAINT uq_m2m_api_key_hash UNIQUE (key_hash)
);

CREATE INDEX IF NOT EXISTS idx_m2m_api_key_client_name ON t_m2m_api_key (client_name);

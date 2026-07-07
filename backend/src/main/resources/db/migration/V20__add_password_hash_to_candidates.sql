-- V20: Add password_hash to candidates for candidate portal auth
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255);

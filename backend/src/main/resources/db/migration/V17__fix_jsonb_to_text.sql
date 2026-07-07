-- V17__fix_jsonb_to_text.sql
-- Converts jsonb settings columns to text for JPA compatibility.
-- jsonb requires special Hibernate type conversions; text works out of the box.

ALTER TABLE tenants ALTER COLUMN settings TYPE TEXT;
ALTER TABLE companies ALTER COLUMN settings TYPE TEXT;

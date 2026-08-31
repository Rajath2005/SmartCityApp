-- Adds soft-delete support to databases created before is_active existed.
-- Existing rows take the column default, so every current account stays active
-- and nobody is locked out by running this migration.
ALTER TABLE users
    ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

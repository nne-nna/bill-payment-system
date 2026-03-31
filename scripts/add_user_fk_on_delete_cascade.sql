-- Run once on PostgreSQL to ensure deleting a user cascades to dependent rows.
-- This is needed for existing databases where constraints were created before
-- cascade rules were added to JPA mappings.

BEGIN;

DO $$
DECLARE
    fk_name text;
BEGIN
    FOR fk_name IN
        SELECT tc.constraint_name
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
          ON tc.constraint_name = kcu.constraint_name
         AND tc.table_schema = kcu.table_schema
        JOIN information_schema.constraint_column_usage ccu
          ON ccu.constraint_name = tc.constraint_name
         AND ccu.table_schema = tc.table_schema
        WHERE tc.constraint_type = 'FOREIGN KEY'
          AND tc.table_name = 'wallet'
          AND kcu.column_name = 'user_id'
          AND ccu.table_name = 'users'
    LOOP
        EXECUTE format('ALTER TABLE wallet DROP CONSTRAINT %I', fk_name);
    END LOOP;
END $$;

ALTER TABLE wallet
    ADD CONSTRAINT fk_wallet_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

DO $$
DECLARE
    fk_name text;
BEGIN
    FOR fk_name IN
        SELECT tc.constraint_name
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
          ON tc.constraint_name = kcu.constraint_name
         AND tc.table_schema = kcu.table_schema
        JOIN information_schema.constraint_column_usage ccu
          ON ccu.constraint_name = tc.constraint_name
         AND ccu.table_schema = tc.table_schema
        WHERE tc.constraint_type = 'FOREIGN KEY'
          AND tc.table_name = 'transactions'
          AND kcu.column_name = 'user_id'
          AND ccu.table_name = 'users'
    LOOP
        EXECUTE format('ALTER TABLE transactions DROP CONSTRAINT %I', fk_name);
    END LOOP;
END $$;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

DO $$
DECLARE
    fk_name text;
BEGIN
    FOR fk_name IN
        SELECT tc.constraint_name
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
          ON tc.constraint_name = kcu.constraint_name
         AND tc.table_schema = kcu.table_schema
        JOIN information_schema.constraint_column_usage ccu
          ON ccu.constraint_name = tc.constraint_name
         AND ccu.table_schema = tc.table_schema
        WHERE tc.constraint_type = 'FOREIGN KEY'
          AND tc.table_name = 'notifications'
          AND kcu.column_name = 'user_id'
          AND ccu.table_name = 'users'
    LOOP
        EXECUTE format('ALTER TABLE notifications DROP CONSTRAINT %I', fk_name);
    END LOOP;
END $$;

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

DO $$
DECLARE
    fk_name text;
BEGIN
    FOR fk_name IN
        SELECT tc.constraint_name
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
          ON tc.constraint_name = kcu.constraint_name
         AND tc.table_schema = kcu.table_schema
        JOIN information_schema.constraint_column_usage ccu
          ON ccu.constraint_name = tc.constraint_name
         AND ccu.table_schema = tc.table_schema
        WHERE tc.constraint_type = 'FOREIGN KEY'
          AND tc.table_name = 'password_reset_tokens'
          AND kcu.column_name = 'user_id'
          AND ccu.table_name = 'users'
    LOOP
        EXECUTE format('ALTER TABLE password_reset_tokens DROP CONSTRAINT %I', fk_name);
    END LOOP;
END $$;

ALTER TABLE password_reset_tokens
    ADD CONSTRAINT fk_password_reset_tokens_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

COMMIT;

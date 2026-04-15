-- projects.client_id must reference client rows in `client_profile` (not `profiles`).
-- Drops FK on projects(client_id) that points at `profiles`, then adds FK to `client_profile`.
--
-- Requires: table `client_profile` with primary key column `client_id` (matches ClientProfile entity).

SET @fk_profiles := (
    SELECT kcu.CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE kcu
             JOIN information_schema.TABLE_CONSTRAINTS tc
                  ON kcu.CONSTRAINT_SCHEMA = tc.CONSTRAINT_SCHEMA
                      AND kcu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME
                      AND kcu.TABLE_NAME = tc.TABLE_NAME
    WHERE kcu.TABLE_SCHEMA = DATABASE()
      AND kcu.TABLE_NAME = 'projects'
      AND kcu.COLUMN_NAME = 'client_id'
      AND kcu.REFERENCED_TABLE_NAME = 'profiles'
      AND tc.CONSTRAINT_TYPE = 'FOREIGN KEY'
    LIMIT 1
);

SET @drop_sql := IF(@fk_profiles IS NOT NULL,
                    CONCAT('ALTER TABLE projects DROP FOREIGN KEY `', @fk_profiles, '`'),
                    'SELECT 1');
PREPARE stmt_drop FROM @drop_sql;
EXECUTE stmt_drop;
DEALLOCATE PREPARE stmt_drop;

SET @already_client_profile := (
    SELECT COUNT(*)
    FROM information_schema.KEY_COLUMN_USAGE kcu
             JOIN information_schema.TABLE_CONSTRAINTS tc
                  ON kcu.CONSTRAINT_SCHEMA = tc.CONSTRAINT_SCHEMA
                      AND kcu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME
                      AND kcu.TABLE_NAME = tc.TABLE_NAME
    WHERE kcu.TABLE_SCHEMA = DATABASE()
      AND kcu.TABLE_NAME = 'projects'
      AND kcu.COLUMN_NAME = 'client_id'
      AND kcu.REFERENCED_TABLE_NAME = 'client_profile'
      AND tc.CONSTRAINT_TYPE = 'FOREIGN KEY'
);

SET @add_sql := IF(@already_client_profile = 0,
                   'ALTER TABLE projects ADD CONSTRAINT fk_projects_client_profile FOREIGN KEY (client_id) REFERENCES `client_profile` (client_id)',
                   'SELECT 1');
PREPARE stmt_add FROM @add_sql;
EXECUTE stmt_add;
DEALLOCATE PREPARE stmt_add;

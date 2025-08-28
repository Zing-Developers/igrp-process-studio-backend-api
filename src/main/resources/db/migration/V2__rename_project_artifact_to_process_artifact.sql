-- 1. Renomear tabelas principais
ALTER TABLE t_project_artifact RENAME TO t_process_artifact;
ALTER TABLE t_project_artifact_aud RENAME TO t_process_artifact_aud;

-- 2. Renomear coluna na tabela t_artifact_variable
ALTER TABLE t_artifact_variable
    RENAME COLUMN project_artifact_id TO process_artifact_id;

-- 3. Remover constraint antiga da coluna project_artifact_id (nome pode variar em cada ambiente)
DO $$
DECLARE
r RECORD;
BEGIN
FOR r IN
SELECT conname
FROM pg_constraint c
         JOIN pg_attribute a
              ON a.attnum = ANY (c.conkey)
                  AND a.attrelid = c.conrelid
WHERE c.conrelid = 't_artifact_variable'::regclass
          AND a.attname = 'process_artifact_id'
          AND c.contype = 'f'
    LOOP
        EXECUTE 'ALTER TABLE t_artifact_variable DROP CONSTRAINT ' || r.conname;
END LOOP;
END$$;

-- 4. Criar nova foreign key explícita
ALTER TABLE t_artifact_variable
    ADD CONSTRAINT fk_artifact_variable_process_artifact
        FOREIGN KEY (process_artifact_id)
            REFERENCES t_process_artifact(id);

-- 5. Renomear coluna na tabela de auditoria
ALTER TABLE t_artifact_variable_aud
    RENAME COLUMN project_artifact_id TO process_artifact_id;

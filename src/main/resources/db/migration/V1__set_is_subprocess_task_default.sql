UPDATE t_process_artifact
SET is_subprocess_task = false
WHERE is_subprocess_task IS NULL;

ALTER TABLE t_process_artifact
    ALTER COLUMN is_subprocess_task SET DEFAULT false;

ALTER TABLE t_process_artifact
    ALTER COLUMN is_subprocess_task SET NOT NULL;

ALTER TABLE product ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

UPDATE product SET version = 0 WHERE true;



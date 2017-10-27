ALTER TABLE configuration ADD max_retries INT DEFAULT 5;
ALTER TABLE configuration ADD block_duration INT DEFAULT 6;

ALTER TABLE configuration_aud ADD max_retries INT DEFAULT 5;
ALTER TABLE configuration_aud ADD block_duration INT DEFAULT 6;
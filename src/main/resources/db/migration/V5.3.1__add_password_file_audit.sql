ALTER TABLE password_file ADD created_at DATETIME;
ALTER TABLE password_file ADD updated_at DATETIME;
ALTER TABLE password_file ADD created_by VARCHAR(255);
ALTER TABLE password_file ADD modified_by VARCHAR(255);
ALTER TABLE password_file ADD version INT(11) DEFAULT 0;
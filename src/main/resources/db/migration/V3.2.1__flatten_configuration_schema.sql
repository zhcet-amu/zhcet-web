ALTER TABLE configuration ADD attendance_threshold INT DEFAULT 75;
ALTER TABLE configuration ADD session VARCHAR(255);
ALTER TABLE configuration ADD url VARCHAR(255) DEFAULT 'http://localhost:8080/';
ALTER TABLE configuration ADD automatic BIT DEFAULT 1;
ALTER TABLE configuration DROP config;
TRUNCATE configuration;
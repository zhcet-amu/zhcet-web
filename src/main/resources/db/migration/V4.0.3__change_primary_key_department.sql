ALTER TABLE zhcet.department MODIFY id BIGINT(20) NOT NULL;
ALTER TABLE zhcet.department DROP PRIMARY KEY;
ALTER TABLE zhcet.department ADD PRIMARY KEY (code);
ALTER TABLE zhcet.department DROP id;
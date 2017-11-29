CREATE TABLE password_file
(
  id VARCHAR(255) PRIMARY KEY,
  link VARCHAR(500) NOT NULL,
  created_time DATETIME DEFAULT NOW() NOT NULL,
  deleted BIT(1) DEFAULT 0 NOT NULL
);

CREATE TABLE password_file_aud
(
  id VARCHAR(255) NOT NULL,
  rev integer not null,
  revtype tinyint,
  link VARCHAR(500),
  created_time DATETIME,
  deleted BIT(1),
  PRIMARY KEY (id, rev)
);
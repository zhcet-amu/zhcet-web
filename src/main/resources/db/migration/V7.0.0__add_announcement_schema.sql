CREATE TABLE announcement
(
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  sender_user_id VARCHAR(255) NOT NULL,
  title VARCHAR(150),
  message VARCHAR(500) NOT NULL,
  scheduled BIT(1) DEFAULT 0,
  sent_time DATETIME DEFAULT NOW(),
  automated BIT(1) DEFAULT FALSE,
  created_at DATETIME,
  updated_at DATETIME,
  created_by VARCHAR(255),
  modified_by VARCHAR(255),
  version int(11) DEFAULT 0,
  CONSTRAINT announcement__sender_fk FOREIGN KEY (sender_user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE announcement_aud
(
  id BIGINT(20) NOT NULL,
  rev integer not null,
  revtype tinyint,
  sender_user_id VARCHAR(255),
  title VARCHAR(150),
  message VARCHAR(500),
  scheduled BIT(1),
  sent_time DATETIME,
  automated BIT(1) DEFAULT FALSE,
  PRIMARY KEY (id, rev)
);

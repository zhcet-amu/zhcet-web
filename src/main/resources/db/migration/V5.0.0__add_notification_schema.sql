CREATE TABLE notification
(
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  sender_user_id VARCHAR(255) NOT NULL,
  title VARCHAR(255),
  message VARCHAR(255) NOT NULL,
  recipient_channel VARCHAR(255) NOT NULL,
  channel_type VARCHAR(255),
  scheduled BIT(1) DEFAULT 0,
  sent_time DATETIME DEFAULT NOW(),
  CONSTRAINT notification__sender_fk FOREIGN KEY (sender_user_id) REFERENCES user_auth (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

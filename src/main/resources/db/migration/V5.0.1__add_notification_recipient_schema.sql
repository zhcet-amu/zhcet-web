CREATE TABLE notification_recipient
(
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  notification_id BIGINT(20) NOT NULL,
  recipient_user_id VARCHAR(255) NOT NULL,
  favorite BIT(1) DEFAULT 0,
  `read` BIT(1) DEFAULT 0,
  read_time DATETIME,
  CONSTRAINT notification_recipient__notification_fk FOREIGN KEY (notification_id) REFERENCES notification (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT notification_recipient__recipient_fk FOREIGN KEY (recipient_user_id) REFERENCES user_auth (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

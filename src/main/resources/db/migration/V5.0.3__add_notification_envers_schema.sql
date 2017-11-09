CREATE TABLE notification_aud
(
  id BIGINT(20) NOT NULL,
  rev integer not null,
  revtype tinyint,
  sender_user_id VARCHAR(255),
  title VARCHAR(255),
  message VARCHAR(255),
  recipient_channel VARCHAR(255),
  channel_type VARCHAR(255),
  scheduled BIT(1),
  sent_time DATETIME,
  PRIMARY KEY (id, rev)
);

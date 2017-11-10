ALTER TABLE notification ADD automated BIT(1) DEFAULT FALSE;
ALTER TABLE notification MODIFY message VARCHAR(500) NOT NULL;
ALTER TABLE notification MODIFY title VARCHAR(150);
ALTER TABLE notification
  MODIFY COLUMN automated BIT(1) DEFAULT FALSE  AFTER sent_time;

ALTER TABLE notification_aud ADD automated BIT(1) DEFAULT FALSE;
ALTER TABLE notification_aud MODIFY message VARCHAR(500) NOT NULL;
ALTER TABLE notification_aud MODIFY title VARCHAR(150);
ALTER TABLE notification_aud
  MODIFY COLUMN automated BIT(1) DEFAULT FALSE  AFTER sent_time;
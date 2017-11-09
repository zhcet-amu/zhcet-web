ALTER TABLE notification ADD created_at DATETIME;
ALTER TABLE notification ADD updated_at DATETIME;
ALTER TABLE notification ADD created_by VARCHAR(255);
ALTER TABLE notification ADD modified_by VARCHAR(255);
ALTER TABLE notification ADD version INT(11) DEFAULT 0;

ALTER TABLE notification_recipient ADD created_at DATETIME;
ALTER TABLE notification_recipient ADD updated_at DATETIME;
ALTER TABLE notification_recipient ADD created_by VARCHAR(255);
ALTER TABLE notification_recipient ADD modified_by VARCHAR(255);
ALTER TABLE notification_recipient ADD version INT(11) DEFAULT 0;
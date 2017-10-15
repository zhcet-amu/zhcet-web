ALTER TABLE user_detail DROP COLUMN address_line2;
ALTER TABLE user_detail CHANGE COLUMN address_line1 address VARCHAR(500);
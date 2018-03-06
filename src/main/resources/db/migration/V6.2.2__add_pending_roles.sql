ALTER TABLE user ADD pending_roles VARCHAR(255);
ALTER TABLE user
  MODIFY COLUMN pending_roles VARCHAR(255) AFTER roles;

ALTER TABLE user_aud ADD pending_roles VARCHAR(255);
ALTER TABLE user_aud
  MODIFY COLUMN pending_roles VARCHAR(255) AFTER roles;
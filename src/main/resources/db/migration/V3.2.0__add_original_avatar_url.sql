ALTER TABLE user_detail ADD original_avatar_url VARCHAR(255) NULL;
ALTER TABLE user_detail
  MODIFY COLUMN original_avatar_url VARCHAR(255) AFTER avatar_url;
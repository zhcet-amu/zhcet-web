ALTER TABLE `password_reset_token`
  ADD COLUMN `used` bit(1) DEFAULT 0;
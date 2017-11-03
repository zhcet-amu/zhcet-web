ALTER TABLE password_reset_token
  ADD CONSTRAINT password_reset_token_user__fk
FOREIGN KEY (user_id) REFERENCES user_auth (user_id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE password_reset_token DROP FOREIGN KEY FK_reset_user_id;
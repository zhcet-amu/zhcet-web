ALTER TABLE verification_token
  ADD CONSTRAINT verification_token_user__fk
FOREIGN KEY (user_id) REFERENCES user_auth (user_id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE verification_token DROP FOREIGN KEY FK_verify_user_id;
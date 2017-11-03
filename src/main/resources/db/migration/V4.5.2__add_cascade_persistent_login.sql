ALTER TABLE persistent_login
  ADD CONSTRAINT persistent_login_user__fk
FOREIGN KEY (username) REFERENCES user_auth (user_id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE persistent_login DROP FOREIGN KEY FK_login_id;
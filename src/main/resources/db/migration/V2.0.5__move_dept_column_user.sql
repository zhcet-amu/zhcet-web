ALTER TABLE zhcet.user_auth ADD department_id BIGINT(20) NOT NULL;
ALTER TABLE zhcet.user_auth
  MODIFY COLUMN department_id BIGINT(20) NOT NULL AFTER roles;

UPDATE user_auth, user_detail
  SET user_auth.department_id = user_detail.department_id
  WHERE user_auth.user_id = user_detail.user_id;

ALTER TABLE zhcet.user_detail DROP FOREIGN KEY FK_user_department;
ALTER TABLE zhcet.user_detail DROP department_id;

ALTER TABLE zhcet.user_auth
  ADD CONSTRAINT user_dept___fk
  FOREIGN KEY (department_id) REFERENCES department (id) ON DELETE CASCADE ON UPDATE CASCADE;
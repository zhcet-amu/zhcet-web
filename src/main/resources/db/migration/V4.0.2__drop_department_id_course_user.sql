ALTER TABLE course DROP FOREIGN KEY FK_course_dept;
ALTER TABLE course DROP department_id;

ALTER TABLE user_auth DROP FOREIGN KEY user_dept___fk;
ALTER TABLE user_auth DROP department_id;
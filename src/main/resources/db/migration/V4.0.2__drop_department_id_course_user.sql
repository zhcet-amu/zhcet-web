ALTER TABLE zhcet.course DROP FOREIGN KEY FK_course_dept;
ALTER TABLE zhcet.course DROP department_id;

ALTER TABLE zhcet.user_auth DROP FOREIGN KEY user_dept___fk;
ALTER TABLE zhcet.user_auth DROP department_id;
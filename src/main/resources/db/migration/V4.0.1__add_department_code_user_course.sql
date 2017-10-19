-- Course Table --

ALTER TABLE course ADD department_code VARCHAR(2);

UPDATE course SET course.department_code =
  (SELECT department.code FROM department WHERE department.id = course.department_id);

ALTER TABLE course
  MODIFY COLUMN department_code VARCHAR(2) NOT NULL AFTER department_id;

ALTER TABLE course
  ADD CONSTRAINT FK_course_dept_code
  FOREIGN KEY (department_code) REFERENCES department (code) ON DELETE CASCADE ON UPDATE CASCADE;


-- User Table --

ALTER TABLE user_auth ADD department_code VARCHAR(2);

UPDATE zhcet.user_auth SET user_auth.department_code =
  (SELECT department.code FROM department WHERE department.id = user_auth.department_id);

ALTER TABLE user_auth
  MODIFY COLUMN department_code VARCHAR(2) NOT NULL AFTER department_id;

ALTER TABLE user_auth
  ADD CONSTRAINT FK_user_dept_code
  FOREIGN KEY (department_code) REFERENCES department (code) ON DELETE CASCADE ON UPDATE CASCADE;

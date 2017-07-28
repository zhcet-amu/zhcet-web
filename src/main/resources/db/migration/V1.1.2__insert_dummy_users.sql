INSERT INTO `user_auth`
  (user_id, name, password, roles, type)
VALUES
  ('dep1234','Department Admin','$2a$10$m5zSlrbt8ABxBqeEo6wynuHQZdOd3YFymKwgE.ojdv587wikysWQC','ROLE_DEPARTMENT_ADMIN','FACULTY'),
  ('fac1234','Faculty Admin','$2a$10$m5zSlrbt8ABxBqeEo6wynuHQZdOd3YFymKwgE.ojdv587wikysWQC','ROLE_FACULTY','FACULTY'),
  ('dean','Dean Admin','$2a$10$GJa6yBssoq/y7lr99ivcdu872rzqSgSeqi6Ec0UU5oUYuz5y5UpEa','ROLE_DEAN_ADMIN','FACULTY'),
  ('GF1032','Areeb Jamal','$2a$10$/vzG3U5DRv6InwKAqQ/tdOUKMjJjBqG906fTxmQoKaAs4/k0KAltW','ROLE_STUDENT','STUDENT');

INSERT INTO `student`
  (enrolment_number, faculty_number, department_id)
VALUES
  ('GF1032','14PEB049',1);

INSERT INTO `faculty_member`
  (faculty_id, department_id)
VALUES
  ('dep1234',1),
  ('fac1234',1),
  ('dean',1);

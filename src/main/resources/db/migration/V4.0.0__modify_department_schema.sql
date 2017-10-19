INSERT IGNORE INTO department
  (name)
VALUES
  ('Architecture'),
  ('Humanities');

ALTER TABLE department ADD code VARCHAR(2);
CREATE UNIQUE INDEX department_code_unique ON department (code);

UPDATE department SET department.code = 'CO' WHERE department.name = 'Computer';
UPDATE department SET department.code = 'EE' WHERE department.name = 'Electrical';
UPDATE department SET department.code = 'EL' WHERE department.name = 'Electronics';
UPDATE department SET department.code = 'CE' WHERE department.name = 'Civil';
UPDATE department SET department.code = 'CH' WHERE department.name = 'Chemical';
UPDATE department SET department.code = 'PK' WHERE department.name = 'Petrochemical';
UPDATE department SET department.code = 'ME' WHERE department.name = 'Mechanical';
UPDATE department SET department.code = 'AM' WHERE department.name = 'Applied Mathematics';
UPDATE department SET department.code = 'AP' WHERE department.name = 'Applied Physics';
UPDATE department SET department.code = 'AC' WHERE department.name = 'Applied Chemistry';
UPDATE department SET department.code = 'AR' WHERE department.name = 'Architecture';
UPDATE department SET department.code = 'HU' WHERE department.name = 'Humanities';

ALTER TABLE department
  MODIFY COLUMN code VARCHAR(2) NOT NULL AFTER id;
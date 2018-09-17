--liquibase formatted sql

--changeset iamareebjamal:insert-departments
INSERT INTO department
  (code, name)
VALUES
  ('CO', 'Computer'),
  ('EE', 'Electrical'),
  ('EL', 'Electronics'),
  ('CE', 'Civil'),
  ('CH', 'Chemical'),
  ('PK', 'Petroleum Studies'),
  ('ME', 'Mechanical'),
  ('AM', 'Applied Mathematics'),
  ('AP', 'Applied Physics'),
  ('AC', 'Applied Chemistry'),
  ('AR', 'Architecture'),
  ('HU', 'Humanities');

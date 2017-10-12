ALTER TABLE course_in_charge ADD CONSTRAINT unique_course_in_charge_section UNIQUE (floated_course_id, in_charge_faculty_id, section);
DROP INDEX unique_course_in_charge ON course_in_charge;
ALTER TABLE `course_in_charge`
  ADD UNIQUE `unique_course_in_charge`(`floated_course_id`, `in_charge_faculty_id`);
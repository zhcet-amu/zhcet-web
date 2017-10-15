CREATE INDEX department_name_index ON department (name);
CREATE INDEX course_active_index ON course (active DESC);
CREATE INDEX course_in_charge_section_index ON course_in_charge (section);
CREATE INDEX faculty_member_working_index ON faculty_member (working DESC);
CREATE INDEX floated_course_session_index ON floated_course (session DESC);
CREATE INDEX student_status_index ON student (status);
CREATE INDEX student_section_index ON student (section);
ALTER TABLE `user_detail`
    DROP FOREIGN KEY `FK_user_department`;
ALTER TABLE `user_detail`
    ADD CONSTRAINT `FK_user_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE `user_detail`
    DROP FOREIGN KEY `FK_user_id`;
ALTER TABLE `user_detail`
    ADD CONSTRAINT `FK_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_auth` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE `student`
    DROP FOREIGN KEY `FK_enrolment_number`;
ALTER TABLE `student`
    ADD CONSTRAINT `FK_enrolment_number` FOREIGN KEY (`enrolment_number`) REFERENCES `user_auth` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE `faculty_member`
    DROP FOREIGN KEY `FK_faculty_id`;
ALTER TABLE `faculty_member`
    ADD CONSTRAINT `FK_faculty_id` FOREIGN KEY (`faculty_id`) REFERENCES `user_auth` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE `course`
    DROP FOREIGN KEY `FK_course_dept`;
ALTER TABLE `course`
    ADD CONSTRAINT `FK_course_dept` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE `floated_course`
    DROP FOREIGN KEY `FK_floated_course_code`;
ALTER TABLE `floated_course`
    ADD CONSTRAINT `FK_floated_course_code` FOREIGN KEY (`course_code`) REFERENCES `course` (`code`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE `course_in_charge`
    DROP FOREIGN KEY `FK_floated_course`;
ALTER TABLE `course_in_charge`
    ADD CONSTRAINT `FK_floated_course` FOREIGN KEY (`floated_course_id`) REFERENCES `floated_course` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE `course_in_charge`
    DROP FOREIGN KEY `FK_floated_in_charge`;
ALTER TABLE `course_in_charge`
    ADD CONSTRAINT `FK_floated_in_charge` FOREIGN KEY (`in_charge_faculty_id`) REFERENCES `faculty_member` (`faculty_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE `course_registration`
    DROP FOREIGN KEY `FK_course_reg_floated`;
ALTER TABLE `course_registration`
    ADD CONSTRAINT `FK_course_reg_floated` FOREIGN KEY (`floated_course_id`) REFERENCES `floated_course` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE `course_registration`
    DROP FOREIGN KEY `FK_course_reg_student`;
ALTER TABLE `course_registration`
    ADD CONSTRAINT `FK_course_reg_student` FOREIGN KEY (`student_enrolment_number`) REFERENCES `student` (`enrolment_number`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE `attendance`
    DROP FOREIGN KEY `FK_attendance_course`;
ALTER TABLE `attendance`
    ADD CONSTRAINT `FK_attendance_course` FOREIGN KEY (`id`) REFERENCES `course_registration` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE `persistent_login`
    DROP FOREIGN KEY `FK_login_id`;
ALTER TABLE `persistent_login`
    ADD CONSTRAINT `FK_login_id` FOREIGN KEY (`username`) REFERENCES `user_auth` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

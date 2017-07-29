CREATE TABLE `user_auth` (
  `user_id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT 0,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `roles` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`)
);

CREATE TABLE `department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT 0,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_dept_name` (`name`)
);

CREATE TABLE `student` (
  `enrolment_number` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT 0,
  `active` bit(1) NOT NULL DEFAULT 1,
  `address_line1` varchar(255) DEFAULT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone_numbers` varchar(255) DEFAULT NULL,
  `faculty_number` varchar(255) DEFAULT NULL,
  `department_id` bigint(20) NOT NULL,
  PRIMARY KEY (`enrolment_number`),
  UNIQUE KEY `UK_student_fac` (`faculty_number`),
  UNIQUE KEY `UK_student_email` (`email`),
  CONSTRAINT `FK_student_dept` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
);

CREATE TABLE `faculty_member` (
  `faculty_id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT 0,
  `active` bit(1) NOT NULL DEFAULT 1,
  `address_line1` varchar(255) DEFAULT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone_numbers` varchar(255) DEFAULT NULL,
  `department_id` bigint(20) NOT NULL,
  PRIMARY KEY (`faculty_id`),
  UNIQUE KEY `UK_faculty_email` (`email`),
  CONSTRAINT `FK_faculty_dept` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
);

CREATE TABLE `course` (
  `code` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT 0,
  `active` bit(1) NOT NULL DEFAULT 1,
  `title` varchar(255) NOT NULL,
  `department_id` bigint(20) NOT NULL,
  PRIMARY KEY (`code`),
  CONSTRAINT `FK_course_dept` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
);

CREATE TABLE `floated_course` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT 0,
  `session` varchar(255) NOT NULL,
  `course_code` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_floated_course_session` (`course_code`,`session`),
  CONSTRAINT `FK_floated_course_code` FOREIGN KEY (`course_code`) REFERENCES `course` (`code`)
);

CREATE TABLE `course_in_charge` (
  `floated_course_id` varchar(255) NOT NULL,
  `in_charge_faculty_id` varchar(255) NOT NULL,
  CONSTRAINT `FK_floated_course` FOREIGN KEY (`floated_course_id`) REFERENCES `floated_course` (`id`),
  CONSTRAINT `FK_floated_in_charge` FOREIGN KEY (`in_charge_faculty_id`) REFERENCES `faculty_member` (`faculty_id`)
);

CREATE TABLE `course_registration` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT 0,
  `floated_course_id` varchar(255) DEFAULT NULL,
  `student_enrolment_number` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_floated_student` (`floated_course_id`,`student_enrolment_number`),
  CONSTRAINT `FK_course_reg_student` FOREIGN KEY (`student_enrolment_number`) REFERENCES `student` (`enrolment_number`),
  CONSTRAINT `FK_course_reg_floated` FOREIGN KEY (`floated_course_id`) REFERENCES `floated_course` (`id`)
);

CREATE TABLE `attendance` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT 0,
  `attended` int(11) NOT NULL,
  `delivered` int(11) NOT NULL,
  `course_registration_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_course_reg` (`course_registration_id`),
  CONSTRAINT `FK_attendance_course_reg` FOREIGN KEY (`course_registration_id`) REFERENCES `course_registration` (`id`)
);

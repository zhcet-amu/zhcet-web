CREATE TABLE `user_auth` (
  `user_id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  `version` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_1t68827l97cwyxo9r1u6t4p7d` (`name`)
);

CREATE TABLE `student` (
  `enrolment_number` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `address_line1` varchar(255) DEFAULT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone_numbers` varchar(255) DEFAULT NULL,
  `faculty_number` varchar(255) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`enrolment_number`),
  UNIQUE KEY `UK_75qw8ona0wve1mbx7g26y2nws` (`faculty_number`),
  CONSTRAINT `FKkh3m8c2tq2tgrgma1iyn7tvmx` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
);

CREATE TABLE `faculty_member` (
  `faculty_id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `address_line1` varchar(255) DEFAULT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone_numbers` varchar(255) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`faculty_id`),
  CONSTRAINT `FKrybeq1djkgpifqtycvtcw95up` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
);

CREATE TABLE `course` (
  `code` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`code`),
  CONSTRAINT `FKi1btm7ma8n3gaj6afdno300wm` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
);

CREATE TABLE `floated_course` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `session` varchar(255) NOT NULL,
  `course_code` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_2ele6b58s8w6mwvn7jwf495ww` (`course_code`,`session`),
  CONSTRAINT `FKro2dikb5sq3tonnt9sqi6nvro` FOREIGN KEY (`course_code`) REFERENCES `course` (`code`)
);

CREATE TABLE `course_in_charge` (
  `floated_course_id` varchar(255) NOT NULL,
  `in_charge_faculty_id` varchar(255) NOT NULL,
  CONSTRAINT `FKdl294l9od8xvxrhdyxelhf5y0` FOREIGN KEY (`floated_course_id`) REFERENCES `floated_course` (`id`),
  CONSTRAINT `FKoe9p7ukv2jhxxwitlibhsciop` FOREIGN KEY (`in_charge_faculty_id`) REFERENCES `faculty_member` (`faculty_id`)
);

CREATE TABLE `course_registration` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `floated_course_id` varchar(255) DEFAULT NULL,
  `student_enrolment_number` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_qiu58rbam4mj8k6a90vw3e2l1` (`floated_course_id`,`student_enrolment_number`),
  CONSTRAINT `FK6j58k5gde8tys6g6teqjfnocs` FOREIGN KEY (`student_enrolment_number`) REFERENCES `student` (`enrolment_number`),
  CONSTRAINT `FKt3loqjyrec7l6qrd9o0quu2e` FOREIGN KEY (`floated_course_id`) REFERENCES `floated_course` (`id`)
);

CREATE TABLE `attendance` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `attended` int(11) NOT NULL,
  `delivered` int(11) NOT NULL,
  `course_registration_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_i31k8u9qjvv3k5fcvuxbikqpu` (`course_registration_id`),
  CONSTRAINT `FK12t73ddays2ylvr2x74ii79d2` FOREIGN KEY (`course_registration_id`) REFERENCES `course_registration` (`id`)
);

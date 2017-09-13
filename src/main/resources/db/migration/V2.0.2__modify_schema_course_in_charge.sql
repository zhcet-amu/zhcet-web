ALTER TABLE zhcet.course_in_charge
  ADD `id` BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  ADD `section` VARCHAR(255) NULL,
  ADD `created_at` datetime DEFAULT NULL,
  ADD `updated_at` datetime DEFAULT NULL,
  ADD `created_by` varchar(255) DEFAULT NULL,
  ADD `modified_by` varchar(255) DEFAULT NULL,
  ADD `version` int(11) DEFAULT 0;
ALTER TABLE zhcet.course_in_charge
  MODIFY COLUMN `id` BIGINT(20) AUTO_INCREMENT FIRST;


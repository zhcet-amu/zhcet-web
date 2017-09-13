ALTER TABLE `student`
    ADD COLUMN `hall_code` VARCHAR(2),
    ADD COLUMN `section` VARCHAR(255),
    ADD COLUMN `registration_year` INTEGER(4),
    ADD COLUMN `status` CHAR(1) DEFAULT 'A';
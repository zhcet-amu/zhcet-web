CREATE TABLE `verification_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) DEFAULT 0,
  `token` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `used` bit(1) DEFAULT 0,
  `expiry` datetime NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_verify_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_auth` (`user_id`)
);
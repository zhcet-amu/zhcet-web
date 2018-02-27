ALTER TABLE user ADD `using2fa` BIT(1) DEFAULT 0;
ALTER TABLE user ADD `totp_secret` VARCHAR(255) NULL;

ALTER TABLE user_aud ADD `using2fa` BIT(1) DEFAULT 0;
ALTER TABLE user_aud ADD `totp_secret` VARCHAR(255) NULL;
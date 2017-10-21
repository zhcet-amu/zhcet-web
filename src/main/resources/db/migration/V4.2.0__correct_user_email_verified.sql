ALTER TABLE user_auth CHANGE active email_verified BIT(1) DEFAULT b'0';
ALTER TABLE user_auth_aud CHANGE active email_verified BIT(1) DEFAULT b'0';
ALTER TABLE user_auth ADD enabled BIT DEFAULT b'1' NOT NULL;
ALTER TABLE user_auth
  MODIFY COLUMN enabled BIT NOT NULL DEFAULT b'1' AFTER version;

ALTER TABLE user_auth_aud ADD enabled BIT DEFAULT b'1' NOT NULL;
ALTER TABLE user_auth_aud
  MODIFY COLUMN enabled BIT NOT NULL DEFAULT b'1' AFTER revtype;
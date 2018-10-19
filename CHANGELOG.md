## Changelog

#### v2.0.2 (unreleased):

- Add workaround for issue https://github.com/spring-projects/spring-boot/issues/14897 re-enabling resource versioning

#### v2.0.1 (18-10-2018):

- Fix bug related to PSQL revered keyword `user` as column name in UploadedImage preventing users to upload avatars
- Add option to disable FCM token with 403 return code. This code is generated if an FCM token is not generated from the registered app
- Upgrade dependencies to Spring Boot 2.0.6 and others

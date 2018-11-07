## Changelog

#### v2.1.0 (07-11-2018):

- Add JS transpiling workflow  
  Merge datatable components  
  Remove PDF and Excel buttons (were too heavy)
- Add workbox service worker
- Update to Spring Boot v2.1.0  
  Remove custom task scheduler
- Fix remember me config resolving error of expired session  
  Change remember me cookie name for consistency
- Break security config into smaller modules  
  Add disabled API security config
- Order inserts and updates in batch queries
- Update dependencies like sentry and modelmapper

##### v2.0.2 (22-10-2018):

- Add workaround for issue https://github.com/spring-projects/spring-boot/issues/14897 re-enabling resource versioning
- Update Gradle to 4.10

##### v2.0.1 (18-10-2018):

- Fix bug related to PSQL revered keyword `user` as column name in UploadedImage preventing users to upload avatars
- Add option to disable FCM token with 403 return code. This code is generated if an FCM token is not generated from the registered app
- Upgrade dependencies to Spring Boot 2.0.6 and others

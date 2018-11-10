## Changelog

##### v2.1.3 (Unreleased):

- No changes

##### v2.1.2 (2018-11-10):

- Add sourcemap URL to bundled JS
- Fix load more faculty members in Department Panel
- Honor valid password specified in Faculty Registration CSV

##### v2.1.1 (2018-11-09):

- Fix old JS paths to new bundled ones
- Fix Default Values for DataTables
- Fix saving user with 2FA disabled
- Minor UI fixes

#### v2.1.0 (2018-11-07):

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

##### v2.0.2 (2018-10-22):

- Add workaround for issue https://github.com/spring-projects/spring-boot/issues/14897 re-enabling resource versioning
- Update Gradle to 4.10

##### v2.0.1 (2018-10-18):

- Fix bug related to PSQL revered keyword `user` as column name in UploadedImage preventing users to upload avatars
- Add option to disable FCM token with 403 return code. This code is generated if an FCM token is not generated from the registered app
- Upgrade dependencies to Spring Boot 2.0.6 and others

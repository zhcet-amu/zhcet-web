# ZHCET College Management

[![CircleCI](https://img.shields.io/circleci/project/github/zhcet-amu/zhcet-web.svg)](https://circleci.com/gh/iamareebjamal/zhcet-web/tree/master)
[![Dependencies](https://www.versioneye.com/user/projects/59e71de00fb24f00314e6c5b/badge.svg?style=flat)](https://www.versioneye.com/user/projects/59e71de00fb24f00314e6c5b)
[![Codacy Grade](https://img.shields.io/codacy/grade/6fe61c2df20f4e8eb80bcb68fd316dc7.svg)](https://www.codacy.com/app/zhcet-oss/zhcet-web)
[![Server](https://img.shields.io/website-up-down-green-red/https/zhcet.herokuapp.com.svg?label=debug-server)](https://zhcet.herokuapp.com)
[![License](https://img.shields.io/github/license/iamareebjamal/zhcet-web.svg)]()
> A spring boot web MVC project for ZHCET college management

# Index
- [Features and Road Map](#features-and-road-map) (Yet to be added)
  - [Features](#features)
    - [Core Features](#core-features)
    - [Additional Features](#additional-features)
  - [Road Map](#road-map)
- [Installation](#installation) - How to install configure, build and run the project
  - [Project Requirements](#project-requirements) - What are the requirements of Project and how to install them
    - [Requirements](#requirements) - Mandatory requirements of the Project
      - [![Java 8](https://img.shields.io/badge/java-8-green.svg?colorB=9575CD)](#java)
      - [![MySQL 5.6](https://img.shields.io/badge/MySQL-5.6-orange.svg)](#mysql)
    - [Feature Requirements](#feature-requirements) - Additional Optional requirements for certain features
      - [Email](#email)
      - [Firebase](#firebase)
  - [Configuration](#configuration) - How to configure project before running **(If you know your stuff, skip to here)**
      - [Database](#database-config)
      - [Email](#email-config)
      - [Firebase](#firebase-config)
      - [Additional](#additional-config)
      - [Sentry *(Optional)*](#sentry-config)
  - [Running](#running) - How to run and build the project
  - [Additional Support](#additional-support) - IDE and Plugins to ease the development
 - [Developers](#developers)

# Installation

## Project Requirements

> **Note :** There are some requirements to be satisfied before the project can be run. These are mostly configurations in the Project itself and thus will require same steps in Windows as well, but for external dependencies, the tutorial will assume that the OS is Linux. Some link for Windows counterpart of instruction may be provided.

### Requirements

Long story short, you need to have `JDK8` and `MySQL` installed for the project. If you are well versed in both technologies and know the steps to satisfy general requirements (MySQL user, database, privileges) for a web project, then you may skip this section entirely to [Feature Requirements](#feature-requirements)

#### Java

[![Java 8](https://img.shields.io/badge/java-8-green.svg?colorB=9575CD)](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)  
As evident by the nature of Project, Java SDK (JDK) needs to be installed to develop the project. There are various articles as to how to install it on various operating systems. but I recommend the use of [sdkman](http://sdkman.io/) as it allows to easily download and manage different java versions and does not require you to fiddle with paths and installations. Once you have got `sdkman` installed, you have to write this command to install latest version of Java (which may be greater than 8 at the time of installation):

```
sdk install java
```

If you are facing any difficulty because of the latest version, you can install the specific version(last version tested to work with the project) using this command:

```
sdk install java 8u144-zulu
```

Once you have verified the install by running `javac -version` and `java -version` that both of them correspond to JDK8, for instance `1.8.0_144`, then you are ready to move on.

**Note:** If you already have existing JDK8 installation or want to install it through any other means, feel free to do so. Just confirm that `java` and `javac` commands are on path and correspond to version 8. 

> For windows users, you may find Windows speific download instructions [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

#### MySQL

[![MySQL 5.6](https://img.shields.io/badge/MySQL-5.6-orange.svg)]()  
Last version of MySQL known to work on the project was `5.7` but any recent version or 5.6 should work as the project only relies on very basic and stable featureset of MySQL. It doesn't matter how you install MySQL and there are countless of tutorials out there for specific Operating Systems, and if you have any MAMP, LAMP or XAMP installation, there is great chance you have it configured already. Verify it by running `mysql --version`

If you don't have it installed, you can follow any of these great guides:
- [MySQL Installation tutorial by DigitalOcean](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-16-04)
- [MySQL Installation tutorial by Linode](https://www.linode.com/docs/databases/mysql/install-mysql-on-ubuntu-14-04)

I have provided 2 guides so you can educate yourself by studying the common bits and patterns and some extra stuff that may be available in any one of them. Once you have thoroughly read the above guides, see the steps you have to perform below

The steps to complete the MySQL setup for the project(refer to above guides for specific instructions) are:

- **Install MySQL Server** - `sudo apt-get upgrade` is **NOT** required but recommended if you have hige amount of data to spare
- **Secure Installation** - This step is optional
- **Create Database** - Create database for the project, for instance, named `zhcet`
- **Create User** - Create a MySQL user for the project, for instance, named `zhcetuser` protected by a password. It is **NOT**  recommended to use root user for the project
- **Grant Privileges** - Grant privileges to this user on database you had created in one of the previous step. For this example, the command would look like - 

```
grant all privileges on zhcet.* to 'springuser'@'localhost';
``` 

***IMPORTANT:*** **DO NOT FORGET** to customize atleast the username and password of the user you create from this tutorial's example for security purposes. Even if you only deploy the project locally, it is recommended to use different parameters from what is used here.

Once you have performed these steps, verify your user permissions by logging in as the created user and creating and dropping a table. If any of the step was not clear, please revisited the two guides above, as they feature all specific instructions of these steps.

From here, you need to remember the name of database you created, the username and password of the user who has privilege on the database.

> For Windows users, please refer to [this](https://dev.mysql.com/doc/refman/5.7/en/windows-installation.html) for installation instructions. All MySQL instructions are same for both operating systems

### Feature Requirements

There are some additional requirements of the project to enable some features which are optional and not required to run the project, but will throw Runtime Error if they are used:

#### Email

Email account information is used to send mails though SMTP for various reasons:
- Verification of email of the user
- Sending of password reset token to the user (forgot password)
- Notification to the user of an kind (attendance update, etc)

If you want to configure email feature for the app, then please produce working email address and password of any email service provider like GMail. 

> For GMail users, you may need to allow less secure access or create an app password if two factor authentication is enabled for the account. Please verify that you have met such requirements

#### Firebase

Firebase is used for:
- Account Profile Picture Upload
- Static File CDN
- Storage of login slideshow image information

For Firebase configuration, you need to have:
- Firebase Database URL
- Firebase Storage Bucket Name
- Service Account JSON file  

Their documentation is pretty extensive and there are various resources how to obtain these.

## Configuration

There are various configuration options for Spring Boot Applications, but here I'll demonstrate 2 most popular ones. You can make the same confuguration using any of the methods listed [here](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)

Simplest way to configure all options for the app would be create a file named `application-secrets.properties` in `src/main/resources` directory with this content:

```
spring.datasource.url=<database_url>
spring.datasource.username=<db_username>
spring.datasource.password=<db_password>

zhcet.email.address=<your_email>
zhcet.email.password=<your_email_password>

zhcet.firebase.database-url=<firebase_database_url>
zhcet.firebase.storage-bucket=<firebase_storage_bucket>

zhcet.url=<optional_url_of_site>
zhcet.salt=<anyrandomstring>
```

It goes without saying that this file must **NEVER** be shared or checked into version control. This applies for all the files and environment variables that will be discussed in this section.

> To not configure any of the feature, just remove that particular line. but as mentioned above, some requirements are mandatory and some optional

**Bonus** - You can also configure these properties by setting the environment variables mentioned in each section below. But the general rule is to capitalize each letter and replace `.` and `-` by `_` For example, to set `zhcet.firebase.database-url` by environment variables, you need to set its value in `ZHCET_FIREBASE_DATABASE_URL`. This feature is provided by Spring Boot and is mentioned [here](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)

All fields required in configuration should be available to you as discussed in previous sections, and if you know how, you can skip to next section but let's go through some examples of them:

> **Note** - Values given below are only for demonstrative puposes and won't work when used

### Database Config

Assuming the previous example mentioned in Requirements Section, and that you are working on localhost, the configuration will look like this:

- `spring.datasource.url`=**`jdbc:mysql://localhost:3306/zhcet?useSSL=false`**
- `spring.datasource.username`=**`zhcetuser`**
- `spring.datasource.password`=**`YourPassword`**

> **Environment Variables** : `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

**BONUS** - You can also use these short hand environment variables to set the properties as they are inline in [application.properties](https://github.com/zhcet-amu/zhcet-web/blob/master/src/main/resources/application.properties):
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

But the values in `application-secret.properties` and above mentioned environment variables will override these. So either use them or these

> For more information on configuring databases in Spring Boot, please refer to [this](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-sql.html)

### Email Config

Assuming a hypothetical email and password:

- `zhcet.email.address`=**`spring@zhcet.io`**
- `zhcet.email.password`=**`securepassword`**

**Note** - If you **DO NOT** configure email, then application will continue to work, but may throw exceptions while using any of the feature mentioned [here](#email).

> **Environment Variables** : `ZHCET_EMAIL_ADDRESS`, `ZHCET_EMAIL_PASSWORD`

### Firebase Config

- `zhcet.firebase.database-url`=**`https://zhcet-backend.firebaseio.com/`**
- `zhcet.firebase.storage-bucket`=**`zhcet-backend.appspot.com`**

**Note** - Only the first option mentioned [here](#firebase) requires special permission and we can't share our firebase account key for allowing any person to upload images to it, uploading avatar throw an error if firebase is not configured. The application will continue to work without this feature as normal

> **Environment Variables** : `ZHCET_FIREBASE_DATABASE_URL`, `ZHCET_FIREBASE_STORAGE_BUCKET`

**IMPORTANT** Along with these, you need to save the `service-account.json` file in `src/main/resources` directory. Do note that the name of file must be **exactly** same as mentioned

**BONUS** - You can set the contents of Service Account JSON in the environment variable `FIREBASE_JSON` and it will work!

### Additional Config

- `zhcet.url`=**`http://localhost:8080/`**
- `zhcet.salt`=**`saltymemem8`**

**Note** - Both these properties have internal default values and don't need to be set but it is **HIGHLY RECOMMENDED** to set the *salt* value as it is used to hash and verify email unsubscribing link for users and other future internal hashing system. If the default one is used instead of any random secret, anyone can unsubscribe any user's email or much worse depending on where it is used internally. While this is non-crucial while local development, but is extremely unsecure behaviour when used in production

> **Environment Variables** : `ZHCET_URL`, `ZHCET_SALT`

### Sentry Config

This is purely **optional**, and only for those who want to add Sentry support. Add `sentry.properties` in `src/main/resources` with general sentry configuration or use any other means of configuring it. Example config:

```
dsn=<your_dsn>
factory=in.ac.amu.zhcet.configuration.sentry.SentryFactory
```

**IMPORTANT** - Do not forget to configure this factory as it extracts the actual user IP from `X-FORWARDED-FOR` header where it is masked by reverse proxies. Not using this may result in fitering of Sentry Event in case reverse proxy masks user IP as `localhost` and obviouslt the reports about the origin of error will be wrong


## Running

The project uses Gradle as dependency management system which provides a wrapper, executing which it automatically installs gradle and all required dependencies for the project. Simply running the following command will spawn the embedded Tomcat Server with the application on `localhost:8080`
```
./gradlew bootRun
```

## Additional Support

I recommend using IntelliJ Idea for the development as it provides incredible Spring Boot support. You won't be needing to go outside the IDE as it provides these features:
- Spring Boot Support
- Java Beans Support
- Thymeleaf Support
- SQL Support
- Database Viewer
- And countless others

**NOTE** - Install Lombok plugin to see auto generated code by lombok

# Developers
[@divs4debu](https://github.com/divsdebu)  
[@iamareebjamal](https://github.com/iamareebjamal)

create table attendance_aud (
  id varchar(255) not null,
  rev integer not null,
  revtype tinyint,
  attended integer,
  delivered integer,
  primary key (id, rev)
) engine=InnoDB;

create table configuration_aud (
  id bigint not null,
  rev integer not null,
  revtype tinyint,
  attendance_threshold integer,
  automatic bit,
  session varchar(255),
  url varchar(255),
  primary key (id, rev)
) engine=InnoDB;

create table course_aud (
  code varchar(255) not null,
  rev integer not null,
  revtype tinyint,
  active bit,
  branch varchar(255),
  category varchar(255),
  class_work_marks integer,
  compulsory bit,
  credits float,
  description longtext,
  final_marks integer,
  finish_year integer,
  lecture_part integer,
  mid_sem_marks integer,
  practical_part integer,
  semester integer,
  start_year integer,
  theory_part integer,
  title varchar(255),
  total_marks integer,
  type varchar(255),
  department_code varchar(255),
  primary key (code, rev)
) engine=InnoDB;

create table course_in_charge_aud (
  id bigint not null,
  rev integer not null,
  revtype tinyint,
  section varchar(255),
  in_charge_faculty_id varchar(255),
  floated_course_id varchar(255),
  primary key (id, rev)
) engine=InnoDB;

create table course_registration_aud (
  id varchar(255) not null,
  rev integer not null,
  revtype tinyint,
  mode char(1),
  floated_course_id varchar(255),
  student_enrolment_number varchar(255),
  primary key (id, rev)
) engine=InnoDB;

create table department_aud (
  code varchar(255) not null,
  rev integer not null,
  revtype tinyint,
  name varchar(255),
  primary key (code, rev)
) engine=InnoDB;

create table faculty_member_aud (
  faculty_id varchar(255) not null,
  rev integer not null,
  revtype tinyint,
  designation varchar(255),
  working bit,
  primary key (faculty_id, rev)
) engine=InnoDB;

create table floated_course_aud (
  id varchar(255) not null,
  rev integer not null,
  revtype tinyint,
  session varchar(255),
  course_code varchar(255),
  primary key (id, rev)
) engine=InnoDB;

create table student_aud (
  enrolment_number varchar(255) not null,
  rev integer not null,
  revtype tinyint,
  faculty_number varchar(255),
  hall_code varchar(255),
  registration_year integer,
  section varchar(255),
  status char(1),
  primary key (enrolment_number, rev)
) engine=InnoDB;

create table user_auth_aud (
  user_id varchar(255) not null,
  rev integer not null,
  revtype tinyint,
  active bit,
  email varchar(255),
  email_unsubscribed bit,
  name varchar(255),
  password varchar(255),
  password_changed bit,
  roles varchar(255),
  type varchar(255),
  department_code varchar(255),
  primary key (user_id, rev)
) engine=InnoDB;

create table user_detail_aud (
  user_id varchar(255) not null,
  rev integer not null,
  revtype tinyint,
  address varchar(255),
  avatar_updated datetime,
  avatar_url varchar(255),
  city varchar(255),
  description longtext,
  original_avatar_url varchar(255),
  phone_numbers varchar(255),
  state varchar(255),
  primary key (user_id, rev)
) engine=InnoDB;

create table user_revision_entity (
  id integer not null AUTO_INCREMENT,
  timestamp bigint not null,
  username varchar(255),
  primary key (id)
) engine=InnoDB;

alter table attendance_aud
  add constraint FKmjqbjwbrr1m7y5vaoa8sdwj1w
foreign key (rev)
references user_revision_entity (id);

alter table configuration_aud
  add constraint FKblqna3jpj1bk4qkbcmll3coeq
foreign key (rev)
references user_revision_entity (id);

alter table course_aud
  add constraint FK1yssaa9o8ga65mopr3a6b0lcb
foreign key (rev)
references user_revision_entity (id);

alter table course_in_charge_aud
  add constraint FKgb2wo9p2jtami33oeqmvg9ui2
foreign key (rev)
references user_revision_entity (id);

alter table course_registration_aud
  add constraint FKmvhqeyj6c08v5re263c6qlysu
foreign key (rev)
references user_revision_entity (id);

alter table department_aud
  add constraint FKl34ajg5jsh7dihrmcrgjnwv85
foreign key (rev)
references user_revision_entity (id);

alter table faculty_member_aud
  add constraint FK5tq3bearlftebo09wa9gqld4j
foreign key (rev)
references user_revision_entity (id);

alter table floated_course_aud
  add constraint FKus4rtbvfdbd1d5fac2xysjrg
foreign key (rev)
references user_revision_entity (id);

alter table student_aud
  add constraint FKpfkufr3ouw2io6sni60koj9dd
foreign key (rev)
references user_revision_entity (id);

alter table user_auth_aud
  add constraint FKbycdexcb8mw7rud8065sxdqn
foreign key (rev)
references user_revision_entity (id);

alter table user_detail_aud
  add constraint FKm7yr1dg8auodmfa8r44eh31d3
foreign key (rev)
references user_revision_entity (id);
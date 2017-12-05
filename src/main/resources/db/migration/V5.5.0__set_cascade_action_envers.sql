ALTER TABLE attendance_aud DROP FOREIGN KEY FKmjqbjwbrr1m7y5vaoa8sdwj1w;
ALTER TABLE attendance_aud
  ADD CONSTRAINT FKmjqbjwbrr1m7y5vaoa8sdwj1w
FOREIGN KEY (rev) REFERENCES user_revision_entity (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE configuration_aud DROP FOREIGN KEY FKblqna3jpj1bk4qkbcmll3coeq;
ALTER TABLE configuration_aud
  ADD CONSTRAINT FKblqna3jpj1bk4qkbcmll3coeq
FOREIGN KEY (rev) REFERENCES user_revision_entity (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE course_aud DROP FOREIGN KEY FK1yssaa9o8ga65mopr3a6b0lcb;
ALTER TABLE course_aud
  ADD CONSTRAINT FK1yssaa9o8ga65mopr3a6b0lcb
FOREIGN KEY (rev) REFERENCES user_revision_entity (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE course_in_charge_aud DROP FOREIGN KEY FKgb2wo9p2jtami33oeqmvg9ui2;
ALTER TABLE course_in_charge_aud
  ADD CONSTRAINT FKgb2wo9p2jtami33oeqmvg9ui2
FOREIGN KEY (rev) REFERENCES user_revision_entity (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE course_registration_aud DROP FOREIGN KEY FKmvhqeyj6c08v5re263c6qlysu;
ALTER TABLE course_registration_aud
  ADD CONSTRAINT FKmvhqeyj6c08v5re263c6qlysu
FOREIGN KEY (rev) REFERENCES user_revision_entity (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE department_aud DROP FOREIGN KEY FKl34ajg5jsh7dihrmcrgjnwv85;
ALTER TABLE department_aud
  ADD CONSTRAINT FKl34ajg5jsh7dihrmcrgjnwv85
FOREIGN KEY (rev) REFERENCES user_revision_entity (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE faculty_member_aud DROP FOREIGN KEY FK5tq3bearlftebo09wa9gqld4j;
ALTER TABLE faculty_member_aud
  ADD CONSTRAINT FK5tq3bearlftebo09wa9gqld4j
FOREIGN KEY (rev) REFERENCES user_revision_entity (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE floated_course_aud DROP FOREIGN KEY FKus4rtbvfdbd1d5fac2xysjrg;
ALTER TABLE floated_course_aud
  ADD CONSTRAINT FKus4rtbvfdbd1d5fac2xysjrg
FOREIGN KEY (rev) REFERENCES user_revision_entity (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE notification_aud
  ADD CONSTRAINT FKe45gru874rkjf84rjjr
FOREIGN KEY (rev) REFERENCES user_revision_entity(id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE student_aud DROP FOREIGN KEY FKpfkufr3ouw2io6sni60koj9dd;
ALTER TABLE student_aud
  ADD CONSTRAINT FKpfkufr3ouw2io6sni60koj9dd
FOREIGN KEY (rev) REFERENCES user_revision_entity (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE user_aud DROP FOREIGN KEY FKbycdexcb8mw7rud8065sxdqn;
ALTER TABLE user_aud
  ADD CONSTRAINT FKbycdexcb8mw7rud8065sxdqn
FOREIGN KEY (rev) REFERENCES user_revision_entity (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE user_detail_aud DROP FOREIGN KEY FKm7yr1dg8auodmfa8r44eh31d3;
ALTER TABLE user_detail_aud
  ADD CONSTRAINT FKm7yr1dg8auodmfa8r44eh31d3
FOREIGN KEY (rev) REFERENCES user_revision_entity (id) ON DELETE CASCADE ON UPDATE CASCADE;
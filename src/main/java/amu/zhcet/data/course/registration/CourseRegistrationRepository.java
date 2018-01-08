package amu.zhcet.data.course.registration;

import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.user.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, String> {

    Optional<CourseRegistration> findByStudent_EnrolmentNumberAndFloatedCourse(String enrolmentNo, FloatedCourse floatedCourse);

    Optional<CourseRegistration> findByStudentAndFloatedCourse(Student student, FloatedCourse floatedCourse);

}

package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, String> {

    Optional<CourseRegistration> findByStudent_EnrolmentNumberAndFloatedCourse(String enrolmentNo, FloatedCourse floatedCourse);

    Optional<CourseRegistration> findByStudentAndFloatedCourse(Student student, FloatedCourse floatedCourse);

}

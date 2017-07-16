package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, String> {

    CourseRegistration findByStudentAndFloatedCourse(Student student, FloatedCourse floatedCourse);

}

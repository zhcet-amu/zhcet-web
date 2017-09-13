package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloatedCourseRepository extends JpaRepository<FloatedCourse, String> {

    FloatedCourse getBySessionAndCourse_Code(String session, String courseCode);

    List<FloatedCourse> getBySessionAndCourse_Department(String session, Department department);

}

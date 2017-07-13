package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloatedCourseRepository extends JpaRepository<FloatedCourse, Long> {

    FloatedCourse getBySessionAndCourse_Id(String session, long courseId);

    List<FloatedCourse> getBySessionAndCourse_Department_NameIgnoreCase(String session, String string);

    List<FloatedCourse> getBySessionAndCourse_Department(String session, Department department);

    List<FloatedCourse> getBySessionAndInCharge(String session, FacultyMember incharge);

}

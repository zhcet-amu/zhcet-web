package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;

public interface FloatedCourseRepository extends DataTablesRepository<FloatedCourse, String> {

    FloatedCourse getBySessionAndCourse(String session, Course course);

    FloatedCourse getBySessionAndCourse_Code(String session, String courseCode);

    List<FloatedCourse> getBySessionAndCourse_Department(String session, Department department);

    List<FloatedCourse> getBySession(String session);

}

package amu.zhcet.data.course.floated;

import amu.zhcet.data.course.Course;
import amu.zhcet.data.department.Department;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FloatedCourseRepository extends DataTablesRepository<FloatedCourse, String> {

    Optional<FloatedCourse> getBySessionAndCourse(String session, Course course);

    Optional<FloatedCourse> getBySessionAndCourse_Code(String session, String courseCode);

    List<FloatedCourse> getBySessionAndCourse_Department(String session, Department department);

    List<FloatedCourse> getBySession(String session);

    List<FloatedCourseLite> getLightBySession(String session);

    List<FloatedCourseLite> getBySessionAndCourse_CodeIn(String session, Collection<String> ids);

}

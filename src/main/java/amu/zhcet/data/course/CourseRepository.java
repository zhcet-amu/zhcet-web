package amu.zhcet.data.course;

import amu.zhcet.data.course.floated.FloatedCourseLite;
import amu.zhcet.data.department.Department;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, String> {

    Optional<Course> findByCode(String code);

    List<Course> findByDepartment(Department department);

    List<Course> findByDepartmentAndActive(Department department, Boolean active);

    List<Course> findAllByCodeIn(Collection<String> ids);

    List<CourseLite> getByCodeIn(Collection<String> ids);

}

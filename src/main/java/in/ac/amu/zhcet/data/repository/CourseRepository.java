package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, String> {

    Optional<Course> findByCode(String code);

    List<Course> findByDepartment(Department department);

    List<Course> findByDepartmentAndActive(Department department, Boolean active);

}

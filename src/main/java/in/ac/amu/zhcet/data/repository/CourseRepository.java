package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Course;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CourseRepository extends CrudRepository<Course, Long> {

    Course findByCode(String code);

    List<Course> findByTitle(String title);

}

package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Student;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Long> {

    Student getByUser_userId(String userId);

}

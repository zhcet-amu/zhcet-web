package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentRepository extends CrudRepository<Student, Long> {

    Student getByEnrolmentNumber(String enrolmentNumber);

    List<Student> getByUserDetails_Department_Name(String departmentName);
}

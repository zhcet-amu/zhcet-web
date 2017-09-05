package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Student;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;

public interface StudentRepository extends DataTablesRepository<Student, Long> {

    Student getByEnrolmentNumber(String enrolmentNumber);

    Student getByFacultyNumber(String facultyNumber);

    List<Student> getByEnrolmentNumberIn(List<String> ids);

    List<Student> findAll();

}

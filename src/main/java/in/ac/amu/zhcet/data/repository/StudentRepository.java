package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Student;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;

public interface StudentRepository extends DataTablesRepository<Student, Long> {

    interface Identifier {
        String getFacultyNumber();
        String getEnrolmentNumber();
    }

    Student getByEnrolmentNumber(String enrolmentNumber);

    Student getByFacultyNumber(String facultyNumber);

    List<Student> getBySectionAndStatus(String section, Character status);

    List<Student> getByEnrolmentNumberIn(List<String> ids);

    List<Identifier> findAllProjectedBy();

    List<Student> findAll();

}

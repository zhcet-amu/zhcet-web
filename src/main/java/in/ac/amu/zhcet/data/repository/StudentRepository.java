package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Student;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends DataTablesRepository<Student, String> {

    interface Identifier {
        String getFacultyNumber();
        String getEnrolmentNumber();
    }

    Optional<Student> getByEnrolmentNumber(String enrolmentNumber);

    Optional<Student> getByFacultyNumber(String facultyNumber);

    List<Student> getBySectionAndStatus(String section, Character status);

    List<Student> getByEnrolmentNumberIn(List<String> ids);

    List<Identifier> findAllProjectedBy();

    List<Student> findAll();

}

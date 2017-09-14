package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Student;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends DataTablesRepository<Student, Long> {

    interface Identifier {
        String getFacultyNumber();
        String getEnrolmentNumber();
    }

    Student getByEnrolmentNumber(String enrolmentNumber);

    Student getByFacultyNumber(String facultyNumber);

    List<Student> getByEnrolmentNumberIn(List<String> ids);

    @Query(value = "SELECT enrolment_number, faculty_number FROM student", nativeQuery = true)
    List<Identifier> findAllIdentifiers();

}

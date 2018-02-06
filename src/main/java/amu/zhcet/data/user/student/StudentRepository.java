package amu.zhcet.data.user.student;

import io.micrometer.core.lang.NonNullApi;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;
import java.util.Optional;

@NonNullApi
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

    List<Identifier> getByFacultyNumberIn(List<String> facultyNumbers);

    List<Student> findAll();

}

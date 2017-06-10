package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Attendance;
import in.ac.amu.zhcet.data.model.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AttendanceRepository extends CrudRepository<Attendance, Long> {

    List<Attendance> findByStudent(Student student);

    List<Attendance> findByStudent_UserId(String facultyNumber);

    List<Attendance> findByCourse_Code(String courseCode);

}

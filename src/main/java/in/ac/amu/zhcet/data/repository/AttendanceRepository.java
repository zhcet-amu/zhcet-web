package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Attendance;
import in.ac.amu.zhcet.data.model.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AttendanceRepository extends CrudRepository<Attendance, Long> {

    List<Attendance> findBySessionAndStudent(String session, Student student);

    List<Attendance> findBySessionAndStudent_UserId(String session, String facultyNumber);

    List<Attendance> findBySessionAndCourse_Code(String session, String courseCode);

}

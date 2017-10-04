package in.ac.amu.zhcet.service.core;

import in.ac.amu.zhcet.data.model.Attendance;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.repository.AttendanceRepository;
import in.ac.amu.zhcet.data.repository.CourseRegistrationRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CourseRegistrationService {

    private final StudentService studentService;
    private final FloatedCourseRepository floatedCourseRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public CourseRegistrationService(StudentService studentService, FloatedCourseRepository floatedCourseRepository, CourseRegistrationRepository courseRegistrationRepository, AttendanceRepository attendanceRepository) {
        this.studentService = studentService;
        this.floatedCourseRepository = floatedCourseRepository;
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional
    public boolean exists(String enrolment, String courseCode) {
        FloatedCourse course = floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseCode);

        return courseRegistrationRepository.existsByFloatedCourseAndStudent_EnrolmentNumber(course, enrolment);
    }

    @Transactional
    public CourseRegistration getByStudentAndCourse(String enrolment, String courseCode) {
        Student student = studentService.getByEnrolmentNumber(enrolment);
        FloatedCourse course = floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseCode);

        return courseRegistrationRepository.findByStudentAndFloatedCourse(student, course);
    }

    @Transactional
    public void setAttendance(CourseRegistration courseRegistration, int delivered, int attended) {
        CourseRegistration registration = courseRegistrationRepository.findOne(courseRegistration.getId());

        Attendance storedAttendance = registration.getAttendance();
        if (storedAttendance == null) {
            storedAttendance = new Attendance(registration, delivered, attended);

            registration.setAttendance(storedAttendance);
            storedAttendance.setCourseRegistration(registration);
            courseRegistrationRepository.save(courseRegistration);
        }

        storedAttendance.setDelivered(delivered);
        storedAttendance.setAttended(attended);
    }

    @Transactional
    public List<Attendance> getAttendanceByStudent(String studentId) {
        return attendanceRepository.getByCourseRegistration_Student_EnrolmentNumberAndCourseRegistration_FloatedCourse_Session(studentId, ConfigurationService.getDefaultSessionCode());
    }
}

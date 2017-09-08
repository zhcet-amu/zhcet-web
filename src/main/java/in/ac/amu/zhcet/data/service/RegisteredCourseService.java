package in.ac.amu.zhcet.data.service;

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
public class RegisteredCourseService {

    private final StudentService studentService;
    private final FloatedCourseRepository floatedCourseRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public RegisteredCourseService(StudentService studentService, FloatedCourseRepository floatedCourseRepository, CourseRegistrationRepository courseRegistrationRepository, AttendanceRepository attendanceRepository) {
        this.studentService = studentService;
        this.floatedCourseRepository = floatedCourseRepository;
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional
    public CourseRegistration registerStudent(FloatedCourse course, String studentId) {
        FloatedCourse stored = floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), course.getCourse().getCode());

        return courseRegistrationRepository.save(new CourseRegistration(studentService.getByEnrolmentNumber(studentId), stored));
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
    public Attendance setAttendance(CourseRegistration courseRegistration, Attendance attendance) {
        CourseRegistration stored = courseRegistrationRepository
                .findByStudentAndFloatedCourse(
                        courseRegistration.getStudent(), courseRegistration.getFloatedCourse()
                );

        Attendance storedAttendance = stored.getAttendance();
        if (storedAttendance == null) {
            storedAttendance = attendance;

            stored.setAttendance(storedAttendance);
            storedAttendance.setCourseRegistration(stored);
            courseRegistrationRepository.save(stored);
        }

        storedAttendance.setDelivered(attendance.getDelivered());
        storedAttendance.setAttended(attendance.getAttended());

        return storedAttendance;
    }

    @Transactional
    public Attendance setAttendance(CourseRegistration courseRegistration, int delivered, int attended) {
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

        return storedAttendance;
    }

    @Transactional
    public List<Attendance> getAttendanceByStudent(String studentId) {
        return attendanceRepository.getByCourseRegistration_Student_EnrolmentNumberAndCourseRegistration_FloatedCourse_Session(studentId, ConfigurationService.getDefaultSessionCode());
    }
}

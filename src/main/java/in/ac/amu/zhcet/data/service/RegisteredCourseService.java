package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.repository.AttendanceRepository;
import in.ac.amu.zhcet.data.repository.CourseRegistrationRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.utils.Utils;
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
        FloatedCourse stored = floatedCourseRepository.getBySessionAndCourse_Code(Utils.getCurrentSession(), course.getCourse().getCode());

        return courseRegistrationRepository.save(new CourseRegistration(studentService.getByEnrolmentNumber(studentId), stored));
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
    public List<Attendance> getAttendanceByStudent(String studentId) {
        return attendanceRepository.getByCourseRegistration_Student_EnrolmentNumberAndCourseRegistration_FloatedCourse_Session(studentId, Utils.getCurrentSession());
    }
}

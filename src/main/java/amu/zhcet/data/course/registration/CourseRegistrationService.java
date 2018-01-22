package amu.zhcet.data.course.registration;

import amu.zhcet.data.attendance.Attendance;
import amu.zhcet.data.attendance.AttendanceUpload;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.user.student.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class CourseRegistrationService {

    private final FloatedCourseService floatedCourseService;
    private final CourseRegistrationRepository courseRegistrationRepository;

    @Autowired
    public CourseRegistrationService(FloatedCourseService floatedCourseService, CourseRegistrationRepository courseRegistrationRepository) {
        this.floatedCourseService = floatedCourseService;
        this.courseRegistrationRepository = courseRegistrationRepository;
    }

    private Optional<CourseRegistration> getByStudentAndCourse(String enrolment, Course course) {
        Optional<FloatedCourse> floatedCourseOptional = floatedCourseService.getFloatedCourse(course);
        return floatedCourseOptional.flatMap(floatedCourse ->
                courseRegistrationRepository.findByStudent_EnrolmentNumberAndFloatedCourse(enrolment, floatedCourse));
    }

    @Transactional
    public void setAttendance(Course course, AttendanceUpload attendanceUpload) {
        Optional<CourseRegistration> registrationOptional = getByStudentAndCourse(attendanceUpload.getEnrolmentNo(), course);

        registrationOptional.ifPresent(registration -> {
            Attendance storedAttendance = registration.getAttendance();
            if (storedAttendance == null) {
                log.warn("Attendance for {} and {} was null!", course, attendanceUpload.getEnrolmentNo());
                storedAttendance = new Attendance(registration, attendanceUpload.getDelivered(), attendanceUpload.getAttended());

                registration.setAttendance(storedAttendance);
                storedAttendance.setCourseRegistration(registration);
                courseRegistrationRepository.save(registration);
            }

            storedAttendance.setDelivered(attendanceUpload.getDelivered());
            storedAttendance.setAttended(attendanceUpload.getAttended());
        });
    }

    public void removeRegistration(FloatedCourse floatedCourse, Student student) {
        courseRegistrationRepository.findByStudentAndFloatedCourse(student, floatedCourse)
                .ifPresent(courseRegistrationRepository::delete);
    }

}

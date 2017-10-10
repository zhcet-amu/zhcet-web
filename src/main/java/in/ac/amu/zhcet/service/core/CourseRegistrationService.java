package in.ac.amu.zhcet.service.core;

import in.ac.amu.zhcet.data.model.Attendance;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.dto.upload.AttendanceUpload;
import in.ac.amu.zhcet.data.repository.AttendanceRepository;
import in.ac.amu.zhcet.data.repository.CourseRegistrationRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
public class CourseRegistrationService {

    private final FloatedCourseRepository floatedCourseRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public CourseRegistrationService(FloatedCourseRepository floatedCourseRepository, CourseRegistrationRepository courseRegistrationRepository, AttendanceRepository attendanceRepository) {
        this.floatedCourseRepository = floatedCourseRepository;
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public CourseRegistration getByStudentAndCourse(String enrolment, String courseCode) {
        FloatedCourse course = floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseCode);
        return courseRegistrationRepository.findByStudent_EnrolmentNumberAndFloatedCourse(enrolment, course);
    }

    @Transactional
    public void setAttendance(String course, AttendanceUpload attendanceUpload) {
        CourseRegistration registration = getByStudentAndCourse(attendanceUpload.getStudent(), course);

        Attendance storedAttendance = registration.getAttendance();
        if (storedAttendance == null) {
            log.warn("Attendance for %s and %s was null!", course, attendanceUpload.getStudent());
            storedAttendance = new Attendance(registration, attendanceUpload.getDelivered(), attendanceUpload.getAttended());

            registration.setAttendance(storedAttendance);
            storedAttendance.setCourseRegistration(registration);
            courseRegistrationRepository.save(registration);
        }

        storedAttendance.setDelivered(attendanceUpload.getDelivered());
        storedAttendance.setAttended(attendanceUpload.getAttended());
    }

    @Transactional
    public List<Attendance> getAttendanceByStudent(String studentId) {
        return attendanceRepository.getByCourseRegistration_Student_EnrolmentNumberAndCourseRegistration_FloatedCourse_Session(studentId, ConfigurationService.getDefaultSessionCode());
    }
}

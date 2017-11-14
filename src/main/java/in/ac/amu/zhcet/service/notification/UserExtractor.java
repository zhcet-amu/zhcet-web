package in.ac.amu.zhcet.service.notification;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.CourseInChargeService;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.service.FacultyService;
import in.ac.amu.zhcet.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
class UserExtractor {

    private final CourseManagementService courseManagementService;
    private final CourseInChargeService courseInChargeService;
    private final StudentService studentService;
    private final FacultyService facultyService;

    @Autowired
    UserExtractor(CourseManagementService courseManagementService, CourseInChargeService courseInChargeService, StudentService studentService, FacultyService facultyService) {
        this.courseManagementService = courseManagementService;
        this.courseInChargeService = courseInChargeService;
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    private static void sendToCourseRegistrations(List<CourseRegistration> courseRegistrations, Consumer<UserAuth> consumer) {
        for (CourseRegistration courseRegistration : courseRegistrations)
            consumer.accept(courseRegistration.getStudent().getUser());
    }

    void fromFloatedCourse(String floatedCourseId, Consumer<UserAuth> consumer) {
        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCode(floatedCourseId);

        if (floatedCourse == null) {
            log.warn("No such floated course exists {}", floatedCourseId);
            return;
        }

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        sendToCourseRegistrations(courseRegistrations, consumer);
    }

    void fromSection(String section, Consumer<UserAuth> consumer) {
        List<Student> students = studentService.getBySection(section);

        for (Student student : students)
            consumer.accept(student.getUser());
    }

    void fromStudentId(String studentId, Consumer<UserAuth> consumer) {
        Student recipient = studentService.getByEnrolmentNumber(studentId);
        if (recipient == null)
            recipient = studentService.getByFacultyNumber(studentId);

        if (recipient == null) {
            log.warn("No student found with ID {}", studentId);
            return;
        }

        consumer.accept(recipient.getUser());
    }

    void fromFacultyId(String facultyId, Consumer<UserAuth> consumer) {
        FacultyMember recipient = facultyService.getById(facultyId);

        if (recipient == null) {
            log.warn("No faculty found with ID {}", facultyId);
            return;
        }

        consumer.accept(recipient.getUser());
    }

    void fromTaughtCourse(String courseId, String inChargeId, Consumer<UserAuth> consumer) {
        FacultyMember facultyMember = facultyService.getById(inChargeId);

        if (facultyMember == null) {
            log.warn("No faculty member found for {}", inChargeId);
            return;
        }

        courseInChargeService.getCourseByFaculty(facultyMember)
                .stream()
                .filter(inCharge ->
                        inCharge.getFloatedCourse().getCourse().getCode().equals(courseId))
                .forEach(inCharge ->
                        sendToCourseRegistrations(courseInChargeService.getCourseRegistrations(inCharge), consumer));
    }

}

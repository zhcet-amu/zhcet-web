package amu.zhcet.core.notification.sending;

import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.data.user.faculty.FacultyService;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.data.user.student.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Service
class UserExtractor {

    private final FloatedCourseService floatedCourseService;
    private final CourseInChargeService courseInChargeService;
    private final StudentService studentService;
    private final FacultyService facultyService;

    @Autowired
    UserExtractor(FloatedCourseService floatedCourseService, CourseInChargeService courseInChargeService, StudentService studentService, FacultyService facultyService) {
        this.floatedCourseService = floatedCourseService;
        this.courseInChargeService = courseInChargeService;
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    private static void sendToCourseRegistrations(List<CourseRegistration> courseRegistrations, Consumer<User> consumer) {
        for (CourseRegistration courseRegistration : courseRegistrations)
            consumer.accept(courseRegistration.getStudent().getUser());
    }

    private <T> void reportMissing(Optional<T> optional, String type, String id) {
        if (!optional.isPresent())
            log.warn("No {} found with ID {}", type, id);
    }

    void fromFloatedCourse(String floatedCourseId, Consumer<User> consumer) {
        Optional<FloatedCourse> floatedCourseOptional = floatedCourseService.getFloatedCourseByCode(floatedCourseId);
        floatedCourseOptional.ifPresent(floatedCourse -> {
            List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
            sendToCourseRegistrations(courseRegistrations, consumer);
        });
        reportMissing(floatedCourseOptional, "floated course", floatedCourseId);
    }

    void fromSection(String section, Consumer<User> consumer) {
        studentService.getBySection(section)
                .forEach(student -> consumer.accept(student.getUser()));
    }

    void fromStudentId(String studentId, Consumer<User> consumer) {
        Optional<Student> recipient = studentService.getByEnrolmentNumber(studentId);
        if (!recipient.isPresent())
            recipient = studentService.getByFacultyNumber(studentId);

        recipient.ifPresent(student -> consumer.accept(student.getUser()));
        reportMissing(recipient, "student", studentId);
    }

    void fromFacultyId(String facultyId, Consumer<User> consumer) {
        Optional<FacultyMember> recipientOptional = facultyService.getById(facultyId);
        recipientOptional.ifPresent(facultyMember -> consumer.accept(facultyMember.getUser()));
        reportMissing(recipientOptional, "faculty", facultyId);
    }

    void fromTaughtCourse(String courseId, String inChargeId, Consumer<User> consumer) {
        Optional<FacultyMember> recipientOptional = facultyService.getById(inChargeId);
        recipientOptional.ifPresent(facultyMember -> {
            courseInChargeService.getCourseByFaculty(facultyMember)
                    .stream()
                    .filter(inCharge ->
                            inCharge.getFloatedCourse().getCourse().getCode().equals(courseId))
                    .forEach(inCharge ->
                            sendToCourseRegistrations(courseInChargeService.getCourseRegistrations(inCharge), consumer));
        });
        reportMissing(recipientOptional, "faculty", inChargeId);
    }

}

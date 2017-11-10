package in.ac.amu.zhcet.service.notification;

import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.user.UserAuth;
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
    private final StudentService studentService;
    private final FacultyService facultyService;

    @Autowired
    UserExtractor(CourseManagementService courseManagementService, StudentService studentService, FacultyService facultyService) {
        this.courseManagementService = courseManagementService;
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    void fromFloatedCourse(String floatedCourseId, Consumer<UserAuth> consumer) {
        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCode(floatedCourseId);

        if (floatedCourse == null) {
            log.warn("No such floated course exists {}", floatedCourseId);
            return;
        }

        List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
        for (CourseRegistration courseRegistration : courseRegistrations)
            consumer.accept(courseRegistration.getStudent().getUser());
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

}

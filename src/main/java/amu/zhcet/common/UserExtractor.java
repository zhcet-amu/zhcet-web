package amu.zhcet.common;

import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseService;
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

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class UserExtractor {

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

    public Stream<User> fromFloatedCourse(String floatedCourseId) {
        return floatedCourseService.getFloatedCourseByCode(floatedCourseId)
                .map(FloatedCourse::getCourseRegistrations)
                .orElse(Collections.emptyList())
                .stream()
                .map(CourseRegistration::getStudent)
                .map(Student::getUser);
    }

    public Stream<User> fromSection(String section) {
        return studentService.getBySection(section)
                .stream()
                .map(Student::getUser);
    }

    public Optional<User> fromStudentId(String studentId) {
        Optional<Student> recipient = studentService.getByEnrolmentNumber(studentId);
        if (!recipient.isPresent())
            recipient = studentService.getByFacultyNumber(studentId);

        return recipient.map(Student::getUser);
    }

    public Optional<User> fromFacultyId(String facultyId) {
        return facultyService.getById(facultyId)
                .map(FacultyMember::getUser);
    }

    public Stream<User> fromTaughtCourse(String courseId, String inChargeId) {
        return facultyService.getById(inChargeId)
                .map(courseInChargeService::getCourseByFaculty)
                .orElse(Collections.emptyList())
                .stream()
                .filter(courseInCharge -> courseInCharge.getFloatedCourse().getCourse().getCode().equals(courseId))
                .flatMap(courseInCharge -> courseInChargeService.getCourseRegistrations(courseInCharge).stream())
                .map(CourseRegistration::getStudent)
                .map(Student::getUser);
    }

}

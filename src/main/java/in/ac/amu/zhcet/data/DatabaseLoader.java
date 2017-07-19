package in.ac.amu.zhcet.data;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.model.base.user.UserDetails;
import in.ac.amu.zhcet.data.repository.*;
import in.ac.amu.zhcet.data.service.RegisteredCourseService;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.FloatedCourseService;
import in.ac.amu.zhcet.data.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseLoader implements ApplicationRunner {

    private final FloatedCourseService floatedCourseService;
    private final RegisteredCourseService registeredCourseService;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentService studentService;
    private final FacultyService facultyService;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);

    @Autowired
    public DatabaseLoader(FloatedCourseService floatedCourseService, RegisteredCourseService registeredCourseService, StudentRepository studentRepository, CourseRepository courseRepository, StudentService studentService, FacultyService facultyService, DepartmentRepository departmentRepository, FacultyRepository facultyRepository) {
        this.floatedCourseService = floatedCourseService;
        this.registeredCourseService = registeredCourseService;
        this.studentService = studentService;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.facultyService = facultyService;
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Department department = new Department();
        department.setName("Computer");
        departmentRepository.save(department);
        logger.info("DepartmentSaved " + department);
        Course course1 = new Course();
        course1.setCode("CO313");
        course1.setTitle("DBMS");
        course1.setDepartment(department);
        courseRepository.save(course1);
        logger.info("Saved Course " + course1.toString());

        Course course2 = new Course();
        course2.setCode("CO324");
        course2.setTitle("Operating Systems");
        course2.setDepartment(department);
        courseRepository.save(course2);
        logger.info("Saved Course " + course2.toString());

        Course course3 = new Course();
        course3.setCode("CO316");
        course3.setTitle("Theory of Computation");
        course3.setDepartment(department);
        courseRepository.save(course3);
        logger.info("Saved Course " + course3.toString());

        Course course4 = new Course();
        course4.setCode("CO356");
        course4.setTitle("Design and Analysis of Algorithms");
        course4.setDepartment(department);
        courseRepository.save(course4);
        logger.info("Saved Course " + course4.toString());

        UserAuth user = new UserAuth("GF1032", "password", "Areeb Jamal", "STUDENT", new String[]{"ROLE_STUDENT"});
        logger.info("Saved user " + user.toString());
        Student student = new Student(user, "14PEB049");
        UserDetails userDetails = new UserDetails();
        userDetails.setDepartment(department);
        student.setUserDetails(userDetails);
        studentService.register(student);
        logger.info("Saved student " + student.toString());

        UserAuth user1 = new UserAuth("1234", "1234", "dpppp", "FACULTY", new String[]{"ROLE_DEPARTMENT_ADMIN", Roles.FACULTY});
        FacultyMember facultyMember = new FacultyMember(user1);
        UserDetails userDetails1 = new UserDetails();
        userDetails1.setDepartment(department);
        facultyMember.setUserDetails(userDetails1);
        facultyService.register(facultyMember);
        List<String> facultyMembers = new ArrayList<>();
        facultyMembers.add(facultyMember.getFacultyId());

        FloatedCourse floatedCourse1 = floatedCourseService.floatCourse(course1, facultyMembers);
        FloatedCourse floatedCourse2 = floatedCourseService.floatCourse(course2);
        FloatedCourse floatedCourse3 = floatedCourseService.floatCourse(course3, facultyMembers);
        FloatedCourse floatedCourse4 = floatedCourseService.floatCourse(course4, facultyMembers);



        CourseRegistration courseRegistration = registeredCourseService.registerStudent(floatedCourse1, student.getEnrolmentNumber());
        CourseRegistration courseRegistration1 = registeredCourseService.registerStudent(floatedCourse2, student.getEnrolmentNumber());
        CourseRegistration courseRegistration2 = registeredCourseService.registerStudent(floatedCourse3, student.getEnrolmentNumber());
        CourseRegistration courseRegistration3 = registeredCourseService.registerStudent(floatedCourse4, student.getEnrolmentNumber());


        Attendance attendance = new Attendance();
        attendance.setAttended(20);
        attendance.setDelivered(30);
        attendance = registeredCourseService.setAttendance(courseRegistration, attendance);
        logger.info("Saved attendance " + attendance.toString());

        Attendance attendance2 = new Attendance();
        attendance2.setAttended(12);
        attendance2.setDelivered(43);
        attendance2 = registeredCourseService.setAttendance(courseRegistration1, attendance2);
        logger.info("Saved attendance " + attendance2.toString());

        Attendance attendance3 = new Attendance();
        attendance3.setAttended(32);
        attendance3.setDelivered(40);
        attendance3 = registeredCourseService.setAttendance(courseRegistration2, attendance3);
        logger.info("Saved attendance " + attendance3.toString());

        Attendance attendance4 = new Attendance();
        attendance4.setAttended(21);
        attendance4.setDelivered(29);
        attendance4 = registeredCourseService.setAttendance(courseRegistration3, attendance4);
        logger.info("Saved attendance " + attendance4.toString());

        UserAuth user2 = new UserAuth("fac22", "pass", "Dp", "FACULTY", new String[]{Roles.DEAN_ADMIN});

        FacultyMember facultyMember1 = new FacultyMember(user2);
        facultyMember.setUserDetails(userDetails);
        facultyService.register(facultyMember1);

        List<FacultyMember> faculties = facultyRepository.getByUserDetails_Department_Name("Computer");
        logger.info("Faculties : are " + faculties.toString());

        List<Attendance> attendances = registeredCourseService.getAttendanceByStudent("GF1032");
        logger.info(attendances.toString());

        List<Course> courses = courseRepository.findByDepartment_Name("Computer");
        logger.info(courses.toString());

        List<Student> students = studentRepository.getByUserDetails_Department_Name("Computer");
        logger.info(students.toString());

        logger.info(studentRepository.getByEnrolmentNumber("GF1032").toString());
    }
}

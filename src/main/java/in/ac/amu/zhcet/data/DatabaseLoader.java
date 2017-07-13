package in.ac.amu.zhcet.data;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.model.base.user.UserDetails;
import in.ac.amu.zhcet.data.repository.*;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.FloatedCourseService;
import in.ac.amu.zhcet.data.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import in.ac.amu.zhcet.utils.Utils;

import java.util.Collections;
import java.util.List;

@Component
public class DatabaseLoader implements ApplicationRunner {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final CourseRepository courseRepository;
    private final StudentService studentService;
    private final FacultyService facultyService;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final FloatedCourseService floatedCourseService;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);

    @Autowired
    public DatabaseLoader(StudentRepository studentRepository, AttendanceRepository attendanceRepository, CourseRepository courseRepository, StudentService studentService, FacultyService facultyService, DepartmentRepository departmentRepository, FacultyRepository facultyRepository, FloatedCourseService floatedCourseService) {
        this.studentService = studentService;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.courseRepository = courseRepository;
        this.facultyService = facultyService;
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
        this.floatedCourseService = floatedCourseService;
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

        String session = Utils.getCurrentSession();

        Attendance attendance = new Attendance();
        attendance.setCourse(course2);
        attendance.setStudent(student);
        attendance.setAttended(20);
        attendance.setDelivered(30);
        attendance.setSession(session);
        attendanceRepository.save(attendance);
        logger.info("Saved attendance " + attendance.toString());

        Attendance attendance2 = new Attendance();
        attendance2.setCourse(course3);
        attendance2.setStudent(student);
        attendance2.setAttended(12);
        attendance2.setDelivered(43);
        attendance2.setSession(session);
        attendanceRepository.save(attendance2);
        logger.info("Saved attendance " + attendance2.toString());

        Attendance attendance3 = new Attendance();
        attendance3.setCourse(course1);
        attendance3.setStudent(student);
        attendance3.setAttended(32);
        attendance3.setDelivered(40);
        attendance3.setSession(session);
        attendanceRepository.save(attendance3);
        logger.info("Saved attendance " + attendance3.toString());

        Attendance attendance4 = new Attendance();
        attendance4.setCourse(course4);
        attendance4.setStudent(student);
        attendance4.setAttended(21);
        attendance4.setDelivered(29);
        attendance4.setSession(session);
        attendanceRepository.save(attendance4);
        logger.info("Saved attendance " + attendance4.toString());

        UserAuth user1 = new UserAuth("fac22", "pass", "Dp", "FACULTY", new String[]{Roles.DEAN_ADMIN});

        FacultyMember facultyMember = new FacultyMember(user1);
        facultyMember.setUserDetails(userDetails);
        facultyService.register(facultyMember);

        List<FacultyMember> faculties = facultyRepository.getByUserDetails_Department_Name("Computer");
        logger.info("Faculties : are "+ faculties.toString());

        List<Attendance> attendances = attendanceRepository.findBySessionAndStudent_User_userId("A17", "14PEB049");
        logger.info(attendances.toString());

        List<Course> courses = courseRepository.findByDepartment_Name("Computer");
        logger.info(courses.toString());
      
        List<Student> students = studentRepository.getByUserDetails_Department_Name("Computer");
        logger.info(students.toString());
      
        logger.info(studentRepository.getByEnrolmentNumber("GF1032").toString());

        floatedCourseService.floatCourse(course2, Collections.singletonList(facultyMember.getFacultyId()));
        logger.info(floatedCourseService.getCurrentFloatedCourses("computer").toString());
    }
}

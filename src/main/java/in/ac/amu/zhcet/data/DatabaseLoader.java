package in.ac.amu.zhcet.data;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import in.ac.amu.zhcet.utils.Utils;

import java.util.List;

@Component
public class DatabaseLoader implements ApplicationRunner {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);

    @Autowired
    public DatabaseLoader(UserRepository userRepository, StudentRepository studentRepository, AttendanceRepository attendanceRepository, CourseRepository courseRepository, DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Department department = new Department();
        department.setDepartmentName("Computer");
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

        User user = new User("14PEB049", "password", "Areeb Jamal", new String[]{"ROLE_STUDENT"});
        user.setDepartment(department);
        userRepository.save(user);
        logger.info("Saved user " + user.toString());
        Student student = new Student(user, "GF1032");
        studentRepository.save(student);
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

        List<Attendance> attendances = attendanceRepository.findBySessionAndStudent_User_userId("A17", "14PEB049");
        logger.info(attendances.toString());

        List<Course> courses = courseRepository.findByDepartment_DepartmentName("Computer");
        logger.info(courses.toString());
      
        List<Student> students = studentRepository.getByUser_Department_DepartmentName("Computer");
        logger.info(student.toString());
      
        logger.info(studentRepository.getByUser_userId("14PEB049").toString());
    }
}

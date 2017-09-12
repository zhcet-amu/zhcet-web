package in.ac.amu.zhcet.service.core;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.repository.CourseRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class FloatedCourseService {

    private final FloatedCourseRepository floatedCourseRepository;
    private final FacultyService facultyService;
    private final StudentService studentService;
    private final CourseRepository courseRepository;

    @Autowired
    public FloatedCourseService(FloatedCourseRepository floatedCourseRepository, FacultyService facultyService, StudentService studentService, CourseRepository courseRepository) {
        this.floatedCourseRepository = floatedCourseRepository;
        this.facultyService = facultyService;
        this.studentService = studentService;
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses(Department department) {
        return courseRepository.findByDepartment(department);
    }

    public List<FloatedCourse> getCurrentFloatedCourses(Department department) {
        return floatedCourseRepository.getBySessionAndCourse_Department(ConfigurationService.getDefaultSessionCode(), department);
    }

    @Transactional
    public Course register(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public FloatedCourse floatCourse(Course course) {
        Course stored = courseRepository.findOne(course.getCode());

        return floatedCourseRepository.save(new FloatedCourse(ConfigurationService.getDefaultSessionCode(), stored));
    }

    @Transactional
    public FloatedCourse floatCourse(Course course, List<String> facultyMembersId) throws IllegalAccessException {
        FloatedCourse stored = floatCourse(course);

        stored.setInCharge(facultyService.getByIds(facultyMembersId));
        return stored;
    }

    @Transactional
    public void addInCharge(String courseId, List<String> facultyMemberIds) {
        FloatedCourse stored = floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseId);

        stored.getInCharge().addAll(facultyService.getByIds(facultyMemberIds));
    }

    @Transactional
    public void registerStudents(String courseId, List<String> studentIds) {
        FloatedCourse stored = floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseId);

        List<Student> students = studentService.getByIds(studentIds);
        List<CourseRegistration> registrations = new ArrayList<>();

        for (Student student : students) {
            CourseRegistration registration = new CourseRegistration();

            registration.setStudent(student);
            registration.setFloatedCourse(stored);
            registration.getAttendance().setId(registration.generateId());
            registrations.add(registration);

        }

        stored.getCourseRegistrations().addAll(registrations);

        floatedCourseRepository.save(stored);
    }

    public List<FloatedCourse> getByFaculty(FacultyMember facultyMember) {
        return floatedCourseRepository.getBySessionAndInCharge(ConfigurationService.getDefaultSessionCode(), facultyMember);
    }

    public FloatedCourse getCourseById(String courseId){
        return floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseId);
    }
}

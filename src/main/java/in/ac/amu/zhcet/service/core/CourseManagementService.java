package in.ac.amu.zhcet.service.core;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.repository.CourseRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.utils.DuplicateException;
import in.ac.amu.zhcet.utils.UpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CourseManagementService {

    private final FloatedCourseRepository floatedCourseRepository;
    private final StudentService studentService;
    private final CourseRepository courseRepository;

    @Autowired
    public CourseManagementService(FloatedCourseRepository floatedCourseRepository, StudentService studentService, CourseRepository courseRepository) {
        this.floatedCourseRepository = floatedCourseRepository;
        this.studentService = studentService;
        this.courseRepository = courseRepository;
    }

    public Course getCourseByCode(String code) {
        return courseRepository.findByCode(code);
    }

    public List<Course> getAllCourses(Department department) {
        return courseRepository.findByDepartment(department);
    }

    public List<Course> getAllActiveCourse(Department department, boolean active) {
        if (!active) // Get all courses
            return courseRepository.findByDepartment(department);

        return courseRepository.findByDepartmentAndActive(department, true);
    }

    public List<FloatedCourse> getCurrentFloatedCourses(Department department) {
        return floatedCourseRepository.getBySessionAndCourse_Department(ConfigurationService.getDefaultSessionCode(), department);
    }

    @Transactional
    public void addCourse(Course course) {
        Course duplicate = courseRepository.findByCode(course.getCode());
        if (duplicate != null)
            throw new DuplicateException("Course", "code", duplicate.getCode(), duplicate);
        courseRepository.save(course);
    }

    @Transactional
    public void saveCourse(Course course) {
        Course managed = courseRepository.findByCode(course.getCode());

        if (managed == null)
            throw new UpdateException("Course Code");

        BeanUtils.copyProperties(course, managed);
        courseRepository.save(managed);
    }

    @Transactional
    public void floatCourse(Course course) {
        Course stored = courseRepository.findOne(course.getCode());
        floatedCourseRepository.save(new FloatedCourse(ConfigurationService.getDefaultSessionCode(), stored));
    }

    @Transactional
    public void registerStudents(String courseId, List<String> studentIds, List<String> modes) {
        if (studentIds.size() != modes.size())
            return;

        FloatedCourse stored = floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseId);

        List<Student> students = studentService.getByIds(studentIds);
        List<CourseRegistration> registrations = new ArrayList<>();

        for (int i = 0; i < students.size(); i++) {
            CourseRegistration registration = new CourseRegistration();

            registration.setStudent(students.get(i));
            registration.setFloatedCourse(stored);
            registration.setMode(modes.get(i).charAt(0));
            registration.getAttendance().setId(registration.generateId());
            registrations.add(registration);
        }

        stored.getCourseRegistrations().addAll(registrations);

        floatedCourseRepository.save(stored);
    }

    public FloatedCourse getFloatedCourseByCode(String courseId){
        return floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseId);
    }

    public void deleteCourse(String id) {
        courseRepository.delete(id);
    }

    public void unfloatCourse(FloatedCourse floatedCourse) {
        log.info(floatedCourse.getId());
        floatedCourseRepository.delete(floatedCourse.getId());
    }
}

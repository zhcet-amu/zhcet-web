package in.ac.amu.zhcet.service;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.repository.CourseRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.service.misc.ConfigurationService;
import in.ac.amu.zhcet.utils.DuplicateException;
import in.ac.amu.zhcet.utils.UpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
public class CourseManagementService {

    private final FloatedCourseRepository floatedCourseRepository;
    private final FacultyService facultyService;
    private final CourseRepository courseRepository;

    @Autowired
    public CourseManagementService(FloatedCourseRepository floatedCourseRepository, FacultyService facultyService, CourseRepository courseRepository) {
        this.floatedCourseRepository = floatedCourseRepository;
        this.facultyService = facultyService;
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
    public void saveCourse(String original, Course course) {
        Course managed = courseRepository.findByCode(course.getCode());

        if (managed == null || !managed.getCode().equals(original)) {
            log.warn("Attempt to change course code: {} to {}", original, course.getCode());
            throw new UpdateException("Course Code");
        }

        BeanUtils.copyProperties(course, managed);
        courseRepository.save(managed);
    }

    public FloatedCourse verifyAndGetCourse(String courseId) {
        FloatedCourse floatedCourse = getFloatedCourseByCode(courseId);
        if (floatedCourse == null || !floatedCourse.getCourse().getDepartment().equals(facultyService.getFacultyDepartment()))
            throw new AccessDeniedException("403");

        return floatedCourse;
    }

    @Transactional
    public void floatCourse(Course course) {
        Course stored = courseRepository.findOne(course.getCode());
        floatedCourseRepository.save(new FloatedCourse(ConfigurationService.getDefaultSessionCode(), stored));
    }

    public FloatedCourse getFloatedCourseByCode(String courseId){
        return floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseId);
    }

    public void deleteCourse(String id) {
        courseRepository.delete(id);
    }

    public void unfloatCourse(FloatedCourse floatedCourse) {
        floatedCourseRepository.delete(floatedCourse.getId());
    }
}

package in.ac.amu.zhcet.service;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.repository.CourseRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.service.config.ConfigurationService;
import in.ac.amu.zhcet.utils.exception.DuplicateException;
import in.ac.amu.zhcet.utils.exception.UpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class CourseManagementService {

    private final FloatedCourseRepository floatedCourseRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public CourseManagementService(FloatedCourseRepository floatedCourseRepository, CourseRepository courseRepository) {
        this.floatedCourseRepository = floatedCourseRepository;
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllActiveCourse(Department department, boolean active) {
        if (!active) // Get all courses
            return courseRepository.findByDepartment(department);

        return courseRepository.findByDepartmentAndActive(department, true);
    }

    public List<FloatedCourse> getCurrentFloatedCourses(Department department) {
        return floatedCourseRepository.getBySessionAndCourse_Department(ConfigurationService.getDefaultSessionCode(), department);
    }

    public static Stream<String> getEmailsFromCourseRegistrations(Stream<CourseRegistration> courseRegistrations) {
        return StudentService.getEmails(courseRegistrations
                .map(CourseRegistration::getStudent));
    }

    @Transactional
    public void addCourse(Course course) {
        Course duplicate = courseRepository.findByCode(course.getCode());
        if (duplicate != null)
            throw new DuplicateException("Course", "code", duplicate.getCode(), duplicate);
        courseRepository.save(course);
    }

    @Transactional
    public void saveCourse(Course original, Course course) {
        if (!original.getCode().equals(course.getCode())) {
            log.warn("Attempt to change course code: {} to {}", original, course.getCode());
            throw new UpdateException("Course Code");
        }

        BeanUtils.copyProperties(course, original);
        courseRepository.save(original);
    }

    @Transactional
    public void floatCourse(Course course) {
        Course stored = courseRepository.findOne(course.getCode());
        floatedCourseRepository.save(new FloatedCourse(ConfigurationService.getDefaultSessionCode(), stored));
    }

    public boolean isFloated(Course course){
        return getFloatedCourse(course) != null;
    }

    /**
     * Protected method for getting floated course, throws AccessDenied exception if floated course does not exist
     * @param course Course
     * @return FloatedCourse for the corresponding course
     */
    @PostAuthorize("isFloated(returnObject)")
    public FloatedCourse getFloatedCourseByCourse(Course course){
        return getFloatedCourse(course);
    }

    /**
     * Unprotected method for getting the floated course, allows null return value
     * @param course Course
     * @return FloatedCourse for the corresponding course
     */
    private FloatedCourse getFloatedCourse(Course course){
        return floatedCourseRepository.getBySessionAndCourse(ConfigurationService.getDefaultSessionCode(), course);
    }

    /**
     * Unprotected method for getting the floated course, allows null return value
     * @param courseCode String course code to be searched
     * @return FloatedCourse for the corresponding code
     */
    public FloatedCourse getFloatedCourseByCode(String courseCode) {
        return floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseCode);
    }

    public void deleteCourse(Course course) {
        courseRepository.delete(course.getCode());
    }

    public void unfloatCourse(FloatedCourse floatedCourse) {
        floatedCourseRepository.delete(floatedCourse.getId());
    }
}

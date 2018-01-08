package amu.zhcet.data.course;

import amu.zhcet.common.error.DuplicateException;
import amu.zhcet.common.error.UpdateException;
import amu.zhcet.common.utils.StringUtils;
import amu.zhcet.data.config.ConfigurationService;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseRepository;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.student.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
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

    public Course getCourse(String code) {
        return courseRepository.findOne(code);
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

    private static void sanitizeCourse(Course course) {
        course.setCode(StringUtils.capitalizeAll(course.getCode()));
        course.setCategory(StringUtils.capitalizeAll(course.getCategory()));
        course.setBranch(StringUtils.capitalizeAll(course.getBranch()));
    }

    @Transactional
    public void addCourse(Course course) {
        Optional<Course> duplicateOptional = courseRepository.findByCode(course.getCode());
        duplicateOptional.ifPresent(duplicate -> {
            throw new DuplicateException("Course", "code", duplicate.getCode(), duplicate);
        });
        sanitizeCourse(course);
        courseRepository.save(course);
    }

    @Transactional
    public void saveCourse(Course original, Course course) {
        if (!original.getCode().equals(course.getCode())) {
            log.warn("Attempt to change course code: {} to {}", original, course.getCode());
            throw new UpdateException("Course Code");
        }

        BeanUtils.copyProperties(course, original);
        sanitizeCourse(original);
        courseRepository.save(original);
    }

    @Transactional
    public void floatCourse(Course course) {
        Course stored = courseRepository.findOne(course.getCode());
        floatedCourseRepository.save(new FloatedCourse(ConfigurationService.getDefaultSessionCode(), stored));
    }

    public boolean isFloated(Course course){
        return getFloatedCourse(course).isPresent();
    }

    public Optional<FloatedCourse> getFloatedCourse(Course course) {
        if (course == null)
            return Optional.empty();
        return floatedCourseRepository.getBySessionAndCourse(ConfigurationService.getDefaultSessionCode(), course);
    }

    public Optional<FloatedCourse> getFloatedCourseByCode(String courseCode) {
        return floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseCode);
    }

    public void deleteCourse(Course course) {
        courseRepository.delete(course.getCode());
    }

    public void unfloatCourse(FloatedCourse floatedCourse) {
        floatedCourseRepository.delete(floatedCourse.getId());
    }

    public void save(FloatedCourse floatedCourse) {
        floatedCourseRepository.save(floatedCourse);
    }
}

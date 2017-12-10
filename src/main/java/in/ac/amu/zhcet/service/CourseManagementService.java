package in.ac.amu.zhcet.service;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.repository.CourseRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.service.config.ConfigurationService;
import in.ac.amu.zhcet.utils.exception.DuplicateException;
import in.ac.amu.zhcet.utils.exception.UpdateException;
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

    @Transactional
    public void addCourse(Course course) {
        Optional<Course> duplicateOptional = courseRepository.findByCode(course.getCode());
        duplicateOptional.ifPresent(duplicate -> {
            throw new DuplicateException("Course", "code", duplicate.getCode(), duplicate);
        });
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
}

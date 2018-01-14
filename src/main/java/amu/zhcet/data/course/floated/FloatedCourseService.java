package amu.zhcet.data.course.floated;

import amu.zhcet.data.config.ConfigurationService;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.student.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class FloatedCourseService {

    private final FloatedCourseRepository floatedCourseRepository;

    @Autowired
    public FloatedCourseService(FloatedCourseRepository floatedCourseRepository) {
        this.floatedCourseRepository = floatedCourseRepository;
    }

    public List<FloatedCourse> getCurrentFloatedCourses(Department department) {
        return floatedCourseRepository.getBySessionAndCourse_Department(ConfigurationService.getDefaultSessionCode(), department);
    }

    public static Stream<String> getEmailsFromCourseRegistrations(Stream<CourseRegistration> courseRegistrations) {
        return StudentService.getEmails(courseRegistrations
                .map(CourseRegistration::getStudent));
    }

    @Transactional
    public void floatCourse(Course course) {
        floatedCourseRepository.save(new FloatedCourse(ConfigurationService.getDefaultSessionCode(), course));
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

    public void unfloatCourse(FloatedCourse floatedCourse) {
        floatedCourseRepository.delete(floatedCourse.getId());
    }

    public void save(FloatedCourse floatedCourse) {
        floatedCourseRepository.save(floatedCourse);
    }

    public static Set<String> getSections(FloatedCourse floatedCourse) {
        if (floatedCourse == null)
            return Collections.emptySet();

        return floatedCourse.getCourseRegistrations().stream()
                .map(courseRegistration -> courseRegistration.getStudent().getSection())
                .collect(Collectors.toSet());
    }
}

package amu.zhcet.data.course;

import amu.zhcet.common.error.DuplicateException;
import amu.zhcet.common.error.UpdateException;
import amu.zhcet.common.utils.StringUtils;
import amu.zhcet.data.department.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
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
    public void updateCourse(Course original, Course course) {
        if (!original.getCode().equals(course.getCode())) {
            log.warn("Attempt to change course code: {} to {}", original, course.getCode());
            throw new UpdateException("Course Code");
        }

        BeanUtils.copyProperties(course, original);
        sanitizeCourse(original);
        courseRepository.save(original);
    }

    public void deleteCourse(Course course) {
        courseRepository.delete(course.getCode());
    }

    private static void sanitizeCourse(Course course) {
        course.setCode(StringUtils.capitalizeAll(course.getCode()));
        course.setCategory(StringUtils.capitalizeAll(course.getCategory()));
        course.setBranch(StringUtils.capitalizeAll(course.getBranch()));
    }

}

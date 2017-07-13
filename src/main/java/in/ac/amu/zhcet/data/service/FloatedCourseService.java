package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.repository.CourseRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class FloatedCourseService {

    private final FloatedCourseRepository floatedCourseRepository;
    private final FacultyService facultyService;
    private final CourseRepository courseRepository;

    @Autowired
    public FloatedCourseService(FloatedCourseRepository floatedCourseRepository, FacultyService facultyService, CourseRepository courseRepository) {
        this.floatedCourseRepository = floatedCourseRepository;
        this.facultyService = facultyService;
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses(Department department) {
        return courseRepository.findByDepartment(department);
    }

    public List<FloatedCourse> getCurrentFloatedCourses(Department department) {
        return floatedCourseRepository.getBySessionAndCourse_Department(Utils.getCurrentSession(), department);
    }

    @Transactional
    public Course register(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public FloatedCourse floatCourse(Course course) {
        Course stored = courseRepository.findOne(course.getId());

        return floatedCourseRepository.save(new FloatedCourse(Utils.getCurrentSession(), stored, stored.getDepartment()));
    }

    @Transactional
    public void floatCourse(Course course, List<String> facultyMembersId) throws IllegalAccessException {
        FloatedCourse stored = floatCourse(course);

        stored.setInCharge(facultyService.getByIds(facultyMembersId));
    }

    @Transactional
    public void addFacultyMembers(Course course, List<String> facultyMemberIds) {
        FloatedCourse stored = floatedCourseRepository.getBySessionAndCourse_Id(Utils.getCurrentSession(), course.getId());

        stored.setInCharge(facultyService.getByIds(facultyMemberIds));
    }
}

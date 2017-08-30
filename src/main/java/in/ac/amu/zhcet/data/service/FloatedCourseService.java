package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.*;
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
        Course stored = courseRepository.findOne(course.getCode());

        return floatedCourseRepository.save(new FloatedCourse(Utils.getCurrentSession(), stored));
    }

    @Transactional
    public FloatedCourse floatCourse(Course course, List<String> facultyMembersId) throws IllegalAccessException {
        FloatedCourse stored = floatCourse(course);

        stored.setInCharge(facultyService.getByIds(facultyMembersId));
        return stored;
    }

    @Transactional
    public void addInCharge(String courseId, List<String> facultyMemberIds) {
        FloatedCourse stored = floatedCourseRepository.getBySessionAndCourse_Code(Utils.getCurrentSession(), courseId);

        stored.getInCharge().addAll(facultyService.getByIds(facultyMemberIds));
    }

    public List<FloatedCourse> getByFaculty(FacultyMember facultyMember) {
        return floatedCourseRepository.getBySessionAndInCharge(Utils.getCurrentSession(), facultyMember);
    }

    public FloatedCourse getCourseById(String courseId){
        return floatedCourseRepository.getBySessionAndCourse_Code(Utils.getCurrentSession(), courseId);
    }
}

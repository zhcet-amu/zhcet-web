package in.ac.amu.zhcet.service.core;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
public class DepartmentAdminService {

    private final FloatedCourseService floatedCourseService;
    private final FacultyService facultyService;
    private final CourseRepository courseRepository;

    @Autowired
    public DepartmentAdminService(FloatedCourseService floatedCourseService, FacultyService facultyService, CourseRepository courseRepository) {
        this.floatedCourseService = floatedCourseService;
        this.facultyService = facultyService;
        this.courseRepository = courseRepository;
    }

    public FacultyMember getFacultyMember() {
        return facultyService.getLoggedInMember();
    }

    public List<FloatedCourse> getFloatedCourses() {
        return floatedCourseService.getCurrentFloatedCourses(FacultyService.getDepartment(getFacultyMember()));
    }

    public List<Course> getAllCourses() {
        return floatedCourseService.getAllCourses(FacultyService.getDepartment(getFacultyMember()));
    }

    public FloatedCourse getCourseById(String id) {
        return floatedCourseService.getCourseById(id);
    }

    @Transactional
    public void registerCourse(Course course) {
        course.setDepartment(FacultyService.getDepartment(getFacultyMember()));
        floatedCourseService.register(course);
    }

    public void floatCourse(Course course, List<String> facultyMembers) throws IllegalAccessException {
        FacultyMember facultyMember = getFacultyMember();

        if (!course.getDepartment().equals(FacultyService.getDepartment(facultyMember)))
            throw new IllegalAccessException("You don't have authority to float course in this department");

        floatedCourseService.floatCourse(course, facultyMembers);
    }

    public void addInCharge(String courseCode, String facultyId) {
        floatedCourseService.addInCharge(courseCode, Collections.singletonList(facultyId));
    }

    public List<FacultyMember> getAllFacultyMembers(){
        return facultyService.getByDepartment(FacultyService.getDepartment(getFacultyMember()));
    }

    public Course findCourseByCode(String code){
        return courseRepository.findByCode(code);
    }
}

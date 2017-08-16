package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static in.ac.amu.zhcet.data.service.FacultyService.getDepartment;

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
        return floatedCourseService.getCurrentFloatedCourses(getDepartment(getFacultyMember()));
    }

    public List<Course> getAllCourses() {
        return floatedCourseService.getAllCourses(getDepartment(getFacultyMember()));
    }

    @Transactional
    public void registerCourse(Course course) {
        course.setDepartment(getDepartment(getFacultyMember()));
        floatedCourseService.register(course);
    }

    public void floatCourse(Course course, List<String> facultyMembers) throws IllegalAccessException {
        FacultyMember facultyMember = getFacultyMember();

        if (!course.getDepartment().equals(getDepartment(facultyMember)))
            throw new IllegalAccessException("You don't have authority to float course in this department");

        floatedCourseService.floatCourse(course, facultyMembers);
    }

    public List<FacultyMember> getAllFacultyMembers(){
        return facultyService.getByDepartment(getDepartment(getFacultyMember()));
    }

    public Course findCourseByCode(String code){
        return courseRepository.findByCode(code);
    }
}

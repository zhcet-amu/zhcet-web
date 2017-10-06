package in.ac.amu.zhcet.service.core;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.repository.CourseInChargeRepository;
import in.ac.amu.zhcet.data.repository.CourseRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.utils.DuplicateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseManagementService {

    private final FloatedCourseRepository floatedCourseRepository;
    private final CourseInChargeRepository courseInChargeRepository;
    private final FacultyService facultyService;
    private final StudentService studentService;
    private final CourseRepository courseRepository;

    @Autowired
    public CourseManagementService(FloatedCourseRepository floatedCourseRepository, CourseInChargeRepository courseInChargeRepository, FacultyService facultyService, StudentService studentService, CourseRepository courseRepository) {
        this.floatedCourseRepository = floatedCourseRepository;
        this.courseInChargeRepository = courseInChargeRepository;
        this.facultyService = facultyService;
        this.studentService = studentService;
        this.courseRepository = courseRepository;
    }

    public Course findCourseByCode(String code) {
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
    public FloatedCourse floatCourse(Course course) {
        Course stored = courseRepository.findOne(course.getCode());

        return floatedCourseRepository.save(new FloatedCourse(ConfigurationService.getDefaultSessionCode(), stored));
    }

    private List<CourseInCharge> fromFacultyIds(FloatedCourse course, List<String> facultyMembersId) {
        return facultyService.getByIds(facultyMembersId)
                .stream()
                .map(facultyMember -> {
                    CourseInCharge courseInCharge = new CourseInCharge();
                    courseInCharge.setFacultyMember(facultyMember);
                    courseInCharge.setFloatedCourse(course);
                    return courseInCharge;
                }).collect(Collectors.toList());
    }

    @Transactional
    public void floatCourse(Course course, List<String> facultyMembersId) throws IllegalAccessException {
        FloatedCourse stored = floatCourse(course);
        stored.setInCharge(fromFacultyIds(stored, facultyMembersId));
    }

    @Transactional
    public void addInCharge(String courseId, List<String> facultyMemberIds) {
        FloatedCourse stored = floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseId);
        stored.getInCharge().addAll(fromFacultyIds(stored, facultyMemberIds));
    }

    @Transactional
    public void registerStudents(String courseId, List<String> studentIds, List<String> modes) {
        if (studentIds.size() != modes.size())
            return;

        FloatedCourse stored = floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseId);

        List<Student> students = studentService.getByIds(studentIds);
        List<CourseRegistration> registrations = new ArrayList<>();

        for (int i = 0; i < students.size(); i++) {
            CourseRegistration registration = new CourseRegistration();

            registration.setStudent(students.get(i));
            registration.setFloatedCourse(stored);
            registration.setMode(modes.get(i).charAt(0));
            registration.getAttendance().setId(registration.generateId());
            registrations.add(registration);
        }

        stored.getCourseRegistrations().addAll(registrations);

        floatedCourseRepository.save(stored);
    }

    public List<FloatedCourse> getByFaculty(FacultyMember facultyMember) {
        return courseInChargeRepository.findByFacultyMemberAndFloatedCourse_Session(facultyMember, ConfigurationService.getDefaultSessionCode())
                .stream()
                .map(CourseInCharge::getFloatedCourse)
                .collect(Collectors.toList());
    }

    public FloatedCourse getCourseAndVerify(String courseId) {
        FloatedCourse floatedCourse = findFloatedCourseByCode(courseId);
        List<FloatedCourse> floatedCourses = getByFaculty(facultyService.getLoggedInMember());
        if (floatedCourse == null || !floatedCourses.contains(floatedCourse))
            throw new AccessDeniedException("403");

        return floatedCourse;
    }

    public FloatedCourse findFloatedCourseByCode(String courseId){
        return floatedCourseRepository.getBySessionAndCourse_Code(ConfigurationService.getDefaultSessionCode(), courseId);
    }

    public boolean isInCharge(List<CourseInCharge> courseInCharges, FacultyMember member) {
        return courseInCharges
                .stream()
                .map(CourseInCharge::getFacultyMember)
                .collect(Collectors.toList())
                .contains(member);
    }

}

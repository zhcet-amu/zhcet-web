package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.base.user.UserDetails;
import in.ac.amu.zhcet.data.repository.FacultyRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final UserService userService;
    private final FloatedCourseRepository floatedCourseRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository, UserService userService, FloatedCourseRepository floatedCourseRepository) {
        this.facultyRepository = facultyRepository;
        this.userService = userService;
        this.floatedCourseRepository = floatedCourseRepository;
    }

    public FacultyMember getById(String facultyId) {
        return facultyRepository.getByFacultyId(facultyId);
    }

    public List<FacultyMember> getByIds(List<String> facultyIds) {
        return facultyRepository.getByFacultyIdIn(facultyIds);
    }

    public List<FacultyMember> getByDepartment(Department department) {
        return facultyRepository.getByUserDetails_Department_Name(department.getName());
    }

    public FacultyMember getLoggedInMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        return getById(userName);
    }

    @Transactional
    public void register(FacultyMember facultyMember) {
        userService.saveUser(facultyMember.getUser());
        facultyRepository.save(facultyMember);
    }

    @Transactional
    public void updateDepartment(String id, Department department) {
        FacultyMember facultyMember = getById(id);

        UserDetails userDetails = facultyMember.getUserDetails();

        userDetails.setDepartment(department);
        facultyMember.setUserDetails(userDetails);
        facultyRepository.save(facultyMember);
    }

    public List<FloatedCourse> getByFaculty(FacultyMember facultyMember) {
        return floatedCourseRepository.getBySessionAndInCharge(Utils.getCurrentSession(), facultyMember);
    }

}

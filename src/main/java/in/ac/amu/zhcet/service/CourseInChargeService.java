package in.ac.amu.zhcet.service;

import com.google.common.base.Strings;
import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.repository.CourseInChargeRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.service.config.ConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseInChargeService {

    private final FacultyService facultyService;
    private final FloatedCourseRepository floatedCourseRepository;
    private final CourseManagementService courseManagementService;
    private final CourseInChargeRepository courseInChargeRepository;

    @Autowired
    public CourseInChargeService(FacultyService facultyService, FloatedCourseRepository floatedCourseRepository, CourseManagementService courseManagementService, CourseInChargeRepository courseInChargeRepository) {
        this.facultyService = facultyService;
        this.floatedCourseRepository = floatedCourseRepository;
        this.courseManagementService = courseManagementService;
        this.courseInChargeRepository = courseInChargeRepository;
    }

    @Transactional
    public void setInCharge(Course course, List<CourseInCharge> courseInCharges) {
        if (courseInCharges == null)
            return;

        FloatedCourse stored = courseManagementService.getFloatedCourse(course);
        if (stored == null)
            return;

        for (CourseInCharge inCharge : stored.getInCharge()) {
            if (!courseInCharges.contains(inCharge))
                courseInChargeRepository.delete(inCharge.getId());
        }

        stored.getInCharge().clear();

        for (CourseInCharge courseInCharge : courseInCharges)
            addInCharge(stored, courseInCharge.getFacultyMember().getFacultyId(), Strings.emptyToNull(courseInCharge.getSection()));
    }

    private void addInCharge(FloatedCourse stored, String facultyId, String section) {
        FacultyMember facultyMember = facultyService.getById(facultyId);
        if (facultyMember == null) {
            log.error("No such faculty member : {}", facultyId);
            return;
        }

        CourseInCharge inCharge = getCourseInCharge(stored, facultyMember, section);
        if (inCharge != null) {
            log.error("No such in charge : {} {} {}", stored.getCourse().getCode(), facultyMember.getFacultyId(), section);
            return;
        }

        CourseInCharge courseInCharge = new CourseInCharge();
        courseInCharge.setFacultyMember(facultyMember);
        courseInCharge.setFloatedCourse(stored);
        courseInCharge.setSection(section);

        stored.getInCharge().add(courseInCharge);
        floatedCourseRepository.save(stored);
    }

    public List<CourseInCharge> getCourseByFaculty(FacultyMember facultyMember) {
        return courseInChargeRepository.findByFacultyMemberAndFloatedCourse_Session(facultyMember, ConfigurationService.getDefaultSessionCode());
    }

    public CourseInCharge getCourseInCharge(FloatedCourse floatedCourse, FacultyMember facultyMember, String section) {
        if (floatedCourse == null || facultyMember == null)
            return null;
        return courseInChargeRepository.findByFloatedCourseAndFacultyMemberAndSection
                (floatedCourse, facultyMember, Strings.emptyToNull(section));
    }

    public CourseInCharge getCourseInCharge(String inChargeCode) {
        List<String> tokens = Arrays.asList(inChargeCode.trim().split(":"));
        if (tokens.size() == 1) {
            FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCode(tokens.get(0));
            return getCourseInCharge(floatedCourse, facultyService.getLoggedInMember(), null);
        } else if (tokens.size() > 1) {
            FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCode(tokens.get(0));
            return getCourseInCharge(floatedCourse, facultyService.getLoggedInMember(), tokens.get(1));
        }

        return null;
    }

    public List<CourseRegistration> getCourseRegistrations(CourseInCharge courseInCharge) {
        String section = Strings.emptyToNull(courseInCharge.getSection());
        if (section == null) // Allow all registrations
            return courseInCharge.getFloatedCourse().getCourseRegistrations();
        else
            return courseInCharge.getFloatedCourse().getCourseRegistrations()
                .stream()
                .filter(courseRegistration -> courseRegistration.getStudent().getSection().equals(section))
                .collect(Collectors.toList());
    }

    public Set<String> getSections(FloatedCourse floatedCourse) {
        if (floatedCourse == null)
            return Collections.emptySet();

        return floatedCourse.getCourseRegistrations().stream()
                .map(courseRegistration -> courseRegistration.getStudent().getSection())
                .collect(Collectors.toSet());
    }

}

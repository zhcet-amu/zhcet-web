package in.ac.amu.zhcet.service;

import in.ac.amu.zhcet.data.model.*;
import in.ac.amu.zhcet.data.repository.CourseInChargeRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

        FloatedCourse stored = courseManagementService.getFloatedCourseByCourse(course);
        if (stored == null)
            return;

        for (CourseInCharge inCharge : stored.getInCharge()) {
            if (!courseInCharges.contains(inCharge))
                courseInChargeRepository.delete(inCharge.getId());
        }

        stored.getInCharge().clear();

        for (CourseInCharge courseInCharge : courseInCharges)
            addInCharge(stored, courseInCharge.getFacultyMember().getFacultyId(), StringUtils.nullIfEmpty(courseInCharge.getSection()));
    }

    private void addInCharge(FloatedCourse stored, String facultyId, String section) {
        FacultyMember facultyMember = facultyService.getById(facultyId);
        if (facultyMember == null) {
            log.error("No such faculty member : {}", facultyId);
            return;
        }

        CourseInCharge inCharge = courseInChargeRepository.findByFloatedCourseAndFacultyMemberAndSection(stored, facultyMember, section);
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

    @PostAuthorize("isCourseInCharge(returnObject)")
    public CourseInCharge getCourseInCharge(Course course, String section) {
        FloatedCourse floatedCourse = courseManagementService.getFloatedCourseByCourse(course);
        return courseInChargeRepository.findByFloatedCourseAndFacultyMemberAndSection
                (floatedCourse, facultyService.getLoggedInMember(), StringUtils.nullIfEmpty(section));
    }

    public List<CourseRegistration> getCourseRegistrations(CourseInCharge courseInCharge) {
        String section = StringUtils.nullIfEmpty(courseInCharge.getSection());
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

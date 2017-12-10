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
import java.util.*;
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

        courseManagementService.getFloatedCourse(course).ifPresent(floatedCourse -> {
            for (CourseInCharge inCharge : floatedCourse.getInCharge()) {
                if (!courseInCharges.contains(inCharge))
                    courseInChargeRepository.delete(inCharge.getId());
            }

            floatedCourse.getInCharge().clear();

            for (CourseInCharge courseInCharge : courseInCharges)
                addInCharge(floatedCourse, courseInCharge.getFacultyMember().getFacultyId(), Strings.emptyToNull(courseInCharge.getSection()));
        });
    }

    private void addInCharge(FloatedCourse stored, String facultyId, String section) {
        Optional<FacultyMember> facultyMemberOptional = facultyService.getById(facultyId);
        facultyMemberOptional.ifPresent(facultyMember -> {
            Optional<CourseInCharge> inChargeOptional = getCourseInCharge(stored, facultyMember, section);
            inChargeOptional.ifPresent(courseInCharge -> log.error("In-charge already present : {} {} {}",
                    stored.getCourse().getCode(), facultyMember.getFacultyId(), section));
            inChargeOptional.orElseGet(() -> {
                CourseInCharge courseInCharge = new CourseInCharge();
                courseInCharge.setFacultyMember(facultyMember);
                courseInCharge.setFloatedCourse(stored);
                courseInCharge.setSection(section);

                stored.getInCharge().add(courseInCharge);
                floatedCourseRepository.save(stored);

                return courseInCharge;
            });
        });
        if (!facultyMemberOptional.isPresent()) {
            log.error("No such faculty member : {}", facultyId);
        }
    }

    public List<CourseInCharge> getCourseByFaculty(FacultyMember facultyMember) {
        return courseInChargeRepository.findByFacultyMemberAndFloatedCourse_Session(facultyMember, ConfigurationService.getDefaultSessionCode());
    }

    public Optional<CourseInCharge> getCourseInCharge(FloatedCourse floatedCourse, FacultyMember facultyMember, String section) {
        if (floatedCourse == null || facultyMember == null)
            return Optional.empty();
        return courseInChargeRepository.findByFloatedCourseAndFacultyMemberAndSection(floatedCourse, facultyMember, Strings.emptyToNull(section));
    }

    public Optional<CourseInCharge> getCourseInCharge(String inChargeCode) {
        List<String> tokens = Arrays.asList(inChargeCode.trim().split(":"));

        if (tokens.size() < 1)
            return Optional.empty();

        String courseCode = tokens.get(0);
        Optional<FacultyMember> facultyMemberOptional = facultyService.getLoggedInMember();
        Optional<FloatedCourse> floatedCourseOptional = courseManagementService.getFloatedCourseByCode(courseCode);
        if (!facultyMemberOptional.isPresent() || !floatedCourseOptional.isPresent())
            return Optional.empty();

        if (tokens.size() == 1) {
            return getCourseInCharge(floatedCourseOptional.get(), facultyMemberOptional.get(), null);
        } else {
            String section = tokens.get(1);
            return getCourseInCharge(floatedCourseOptional.get(), facultyMemberOptional.get(), section);
        }
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

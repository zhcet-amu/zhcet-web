package amu.zhcet.data.course.incharge;

import amu.zhcet.data.config.ConfigurationService;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.CourseManagementService;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseRepository;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.data.user.faculty.FacultyService;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
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

    /**
     * Splits the in-charge code into a pair.
     * @param code String in-charge of the form courseCode:section -> In-charge teaches section of courseCode
     *             Absence of section means in-charge teaches all sections
     * @return A pair of strings with first element denoting the course code and second the section
     *         If second is null, it means section was not found or was empty
     *         If both are null, it means the code was invalid
     */
    public static Pair<String, String> getCodeAndSection(String code) {
        String[] splitted = code.trim().split(":");

        String left = null;
        String right = null;

        if (splitted.length > 1) {
            left = splitted[0];
            right = splitted[1];
        } else if (splitted.length == 1) {
            left = splitted[0];
        }

        return Pair.of(left, right);
    }

    static boolean isInvalidCodeAndSection(Pair<String, String> codeAndSection) {
        return codeAndSection.getLeft() == null;
    }

    public Optional<CourseInCharge> getCourseInCharge(String inChargeCode) {
        Pair<String, String> codeAndSection = getCodeAndSection(inChargeCode);

        if (isInvalidCodeAndSection(codeAndSection))
            return Optional.empty();

        String courseCode = codeAndSection.getLeft();
        String section = codeAndSection.getRight();

        Optional<FacultyMember> facultyMemberOptional = facultyService.getLoggedInMember();
        Optional<FloatedCourse> floatedCourseOptional = courseManagementService.getFloatedCourseByCode(courseCode);
        if (!facultyMemberOptional.isPresent() || !floatedCourseOptional.isPresent())
            return Optional.empty();

        return getCourseInCharge(floatedCourseOptional.get(), facultyMemberOptional.get(), section);
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

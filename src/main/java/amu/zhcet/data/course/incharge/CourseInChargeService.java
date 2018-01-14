package amu.zhcet.data.course.incharge;

import amu.zhcet.data.config.ConfigurationService;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.data.user.faculty.FacultyService;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseInChargeService {

    private final FacultyService facultyService;
    private final FloatedCourseService floatedCourseService;
    private final CourseInChargeRepository courseInChargeRepository;

    @Autowired
    public CourseInChargeService(FacultyService facultyService, FloatedCourseService floatedCourseService, CourseInChargeRepository courseInChargeRepository) {
        this.facultyService = facultyService;
        this.floatedCourseService = floatedCourseService;
        this.courseInChargeRepository = courseInChargeRepository;
    }

    public static boolean sameCourseInCharge(CourseInCharge c1, CourseInCharge c2) {
        return c1.getFacultyMember().equals(c2.getFacultyMember()) && c1.getFloatedCourse().equals(c2.getFloatedCourse());
    }

    @Transactional
    public void deleteCourseInCharge(CourseInCharge courseInCharge) {
        log.info("Deleting Course In-Charge : {}", courseInCharge);
        courseInChargeRepository.delete(courseInCharge);
    }

    @Transactional
    public void saveCourseInCharge(CourseInCharge courseInCharge) {
        log.info("Saving Course In-Charge : {}", courseInCharge);
        courseInChargeRepository.save(courseInCharge);
    }

    public List<CourseInCharge> getCourseByFaculty(FacultyMember facultyMember) {
        return courseInChargeRepository.findByFacultyMemberAndFloatedCourse_Session(facultyMember, ConfigurationService.getDefaultSessionCode());
    }

    private Optional<CourseInCharge> getCourseInCharge(FloatedCourse floatedCourse, FacultyMember facultyMember, String section) {
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

    private static boolean isInvalidCodeAndSection(Pair<String, String> codeAndSection) {
        return codeAndSection.getLeft() == null;
    }

    public Optional<CourseInCharge> getCourseInCharge(String inChargeCode) {
        Pair<String, String> codeAndSection = getCodeAndSection(inChargeCode);

        if (isInvalidCodeAndSection(codeAndSection))
            return Optional.empty();

        String courseCode = codeAndSection.getLeft();
        String section = codeAndSection.getRight();

        Optional<FacultyMember> facultyMemberOptional = facultyService.getLoggedInMember();
        Optional<FloatedCourse> floatedCourseOptional = floatedCourseService.getFloatedCourseByCode(courseCode);

        if (facultyMemberOptional.isPresent() && floatedCourseOptional.isPresent())
            return getCourseInCharge(floatedCourseOptional.get(), facultyMemberOptional.get(), section);

        return Optional.empty();
    }

    public List<CourseRegistration> getCourseRegistrations(CourseInCharge courseInCharge) {
        String section = Strings.emptyToNull(courseInCharge.getSection());
        if (section == null) // Allow all registrations
            return courseInCharge
                    .getFloatedCourse()
                    .getCourseRegistrations();
        else
            return courseInCharge
                    .getFloatedCourse()
                    .getCourseRegistrations()
                    .stream()
                    .filter(courseRegistration -> courseRegistration.getStudent().getSection().equals(section))
                    .collect(Collectors.toList());
    }

}

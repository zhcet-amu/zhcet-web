package amu.zhcet.core.admin.department.floated.incharge;

import amu.zhcet.common.utils.DiffUtils;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.data.user.faculty.FacultyMember;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
class InChargeManagementService {

    private final CourseInChargeService courseInChargeService;
    private final DiffUtils<CourseInCharge> inChargeDiffUtils;

    @Autowired
    InChargeManagementService(CourseInChargeService courseInChargeService) {
        this.courseInChargeService = courseInChargeService;
        this.inChargeDiffUtils = DiffUtils.of(CourseInCharge.class)
                .areItemsSame(CourseInChargeService::sameCourseInCharge)
                .build();
    }

    @Transactional
    public void saveInCharge(FloatedCourse floatedCourse, List<FacultyMember> facultyMembers, List<String> sections) {
        Set<CourseInCharge> courseInCharges = new HashSet<>(floatedCourse.getInCharge());
        inChargeDiffUtils.sets(courseInCharges, createNewInCharges(floatedCourse, facultyMembers, sections))
                .calculate(
                        courseInChargeService::saveCourseInCharge,
                        courseInChargeService::deleteCourseInCharge,
                        sameInCharge -> { /* Do nothing */ },
                        (oldInCharge, newInCharge) -> {
                            log.info("Changing section of {} to {}", oldInCharge, newInCharge.getSection());
                            oldInCharge.setSection(newInCharge.getSection());
                            courseInChargeService.saveCourseInCharge(oldInCharge);
                        }
                );
    }

    private void nullifySections(List<String> sections) {
        for (int i = 0; i < sections.size(); i++)
            if (Strings.isNullOrEmpty(sections.get(i)))
                sections.set(i, null);
    }

    // Resize sections list to a predetermined size
    private List<String> getNormalizedSection(List<String> sections, int size) {
        if (sections == null) {
            return Collections.nCopies(size, null);
        } else if (sections.size() < size) {
            nullifySections(sections);
            while (sections.size() != size)
                sections.add(null);
        } else {
            nullifySections(sections);
        }

        return sections;
    }

    private Set<CourseInCharge> createNewInCharges(FloatedCourse floatedCourse, List<FacultyMember> facultyMembers, List<String> sections) {
        if (facultyMembers == null || (sections != null && facultyMembers.size() < sections.size()))
            return null;

        List<String> normalizedSections = getNormalizedSection(sections, facultyMembers.size());

        Set<CourseInCharge> courseInCharges = new HashSet<>(facultyMembers.size());
        for (int i = 0; i < facultyMembers.size(); i++)
            courseInCharges.add(getInCharge(floatedCourse, facultyMembers.get(i), normalizedSections.get(i)));

        return courseInCharges;
    }

    private CourseInCharge getInCharge(FloatedCourse floatedCourse, FacultyMember facultyMember, String section) {
        CourseInCharge courseInCharge = new CourseInCharge();
        courseInCharge.setFloatedCourse(floatedCourse);
        courseInCharge.setFacultyMember(facultyMember);
        courseInCharge.setSection(section);

        return courseInCharge;
    }
}

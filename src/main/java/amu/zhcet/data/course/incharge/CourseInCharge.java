package amu.zhcet.data.course.incharge;

import amu.zhcet.common.model.BaseIdEntity;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.user.faculty.FacultyMember;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@Slf4j
@Entity
@Audited
public class CourseInCharge extends BaseIdEntity {

    @NotNull
    @ManyToOne
    private FloatedCourse floatedCourse;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "in_charge_faculty_id")
    private FacultyMember facultyMember;

    private String section;

    public String getCode() {
        String course = getFloatedCourse().getCourse().getCode();
        String section = getSection();
        String suffix = Strings.emptyToNull(section) == null ? "" : ":" + section;
        return course + suffix;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CourseInCharge))
            return false;

        CourseInCharge inCharge2 = (CourseInCharge) o;
        if (!getFacultyMember().getFacultyId().equals(inCharge2.getFacultyMember().getFacultyId()))
            return false;
        if (getSection() == null && inCharge2.getSection() == null)
            return true;
        if ((getSection() == null && inCharge2.getSection() != null) || (getSection() != null && inCharge2.getSection() == null))
            return false;
        return (getSection().equals(inCharge2.getSection()));
    }

}

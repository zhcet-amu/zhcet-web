package amu.zhcet.data.course.incharge;

import amu.zhcet.common.model.BaseIdEntity;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.user.faculty.FacultyMember;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@Slf4j
@Entity
@Audited
@EqualsAndHashCode(of = {"floatedCourse", "facultyMember", "section"}, callSuper = false)
@ToString(of = {"facultyMember", "floatedCourse", "section"}, callSuper = true)
public class CourseInCharge extends BaseIdEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private FloatedCourse floatedCourse;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "in_charge_faculty_id")
    private FacultyMember facultyMember;

    private String section;

    public String getCode() {
        String course = getFloatedCourse().getCourse().getCode();
        String section = getSection();
        String suffix = Strings.emptyToNull(section) == null ? "" : ":" + section;
        return course + suffix;
    }

}

package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.BaseIdEntity;
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

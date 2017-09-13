package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.BaseIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class CourseInCharge extends BaseIdEntity {

    @NotNull
    @ManyToOne
    private FloatedCourse floatedCourse;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "in_charge_faculty_id")
    private FacultyMember facultyMember;

    private String section;

}

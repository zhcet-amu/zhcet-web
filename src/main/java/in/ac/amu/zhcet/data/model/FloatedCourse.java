package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import in.ac.amu.zhcet.utils.Utils;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "courseRegistrations")
@EqualsAndHashCode(callSuper = true)
public class FloatedCourse extends BaseEntity implements Serializable {
    @Id
    private String id;

    @NaturalId
    @NotNull
    @ManyToOne
    private Course course;
    @NotNull
    @NaturalId
    private String session;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "COURSE_IN_CHARGE")
    private List<FacultyMember> inCharge;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "floatedCourse")
    private List<CourseRegistration> courseRegistrations;

    public FloatedCourse(String session, Course course) {
        this.session = session;
        this.course = course;
    }

    @PrePersist
    public void setDefaults() {
        if (session == null)
            session = Utils.getCurrentSession();

        id = session + "_" + course.getCode();
    }
}

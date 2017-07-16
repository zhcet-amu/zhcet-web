package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import in.ac.amu.zhcet.data.model.base.key.Session;
import in.ac.amu.zhcet.utils.Utils;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "courseRegistrations")
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints=
@UniqueConstraint(columnNames={"course_code", "session"}))
@IdClass(Session.class)
public class FloatedCourse extends BaseEntity implements Serializable {
    @Id
    private Course course;
    @Id
    private String session;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "COURSE_IN_CHARGE",
            joinColumns = {
                    @JoinColumn(name = "course_code"),
                    @JoinColumn(name = "session")
            }
    )
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
    }
}

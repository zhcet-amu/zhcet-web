package amu.zhcet.data.course.floated;

import amu.zhcet.common.model.BaseEntity;
import amu.zhcet.data.config.ConfigurationService;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.course.registration.CourseRegistration;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Audited
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"id", "course"})
@EqualsAndHashCode(callSuper = true)
public class FloatedCourse extends BaseEntity implements Serializable {
    @Id
    @NotBlank
    private String id;

    @NaturalId
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;
    @NotBlank
    @NaturalId
    private String session;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "floatedCourse")
    private List<CourseInCharge> inCharge;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "floatedCourse")
    private List<CourseRegistration> courseRegistrations;

    public FloatedCourse(String session, Course course) {
        this.session = session;
        this.course = course;
    }

    @PrePersist
    public void setDefaults() {
        if (session == null)
            session = ConfigurationService.getDefaultSessionCode();

        id = session + ":" + course.getCode();
    }
}

package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import in.ac.amu.zhcet.data.service.ConfigurationService;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.NotBlank;

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
    @NotBlank
    private String id;

    @NaturalId
    @NotNull
    @ManyToOne
    private Course course;
    @NotBlank
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
            session = ConfigurationService.getDefaultSessionCode();

        id = session + "_" + course.getCode();
    }
}

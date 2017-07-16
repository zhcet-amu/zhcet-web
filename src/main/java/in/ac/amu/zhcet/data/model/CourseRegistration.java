package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CourseRegistration extends BaseEntity implements Serializable {
    @Id
    private String id;

    @NaturalId
    @NonNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private FloatedCourse floatedCourse;

    @NaturalId
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Student student;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER, mappedBy = "courseRegistration")
    private Attendance attendance = new Attendance();

    public CourseRegistration(Student student, FloatedCourse floatedCourse) {
        this.student = student;
        this.floatedCourse = floatedCourse;
    }

    @PrePersist
    public void setRelation() {
        id = floatedCourse.getId() + "_" + student.getEnrolmentNumber();
        attendance.setCourseRegistration(this);
    }

}

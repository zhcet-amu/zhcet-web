package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude="attendance")
public class CourseRegistration extends BaseEntity {
    @Id
    private String id;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    private FloatedCourse floatedCourse;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Student student;

    private char mode;

    @Valid
    @NotNull
    @PrimaryKeyJoinColumn
    @OneToOne(cascade = CascadeType.ALL)
    private Attendance attendance = new Attendance();

    public CourseRegistration(Student student, FloatedCourse floatedCourse) {
        this.student = student;
        this.floatedCourse = floatedCourse;
    }

    public String generateId() {
        return floatedCourse.getId() + ":" + student.getEnrolmentNumber();
    }

    @PrePersist
    public void setRelation() {
        id = floatedCourse.getId() + ":" + student.getEnrolmentNumber();
        attendance.setCourseRegistration(this);
    }

}

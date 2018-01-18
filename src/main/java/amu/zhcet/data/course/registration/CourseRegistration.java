package amu.zhcet.data.course.registration;

import amu.zhcet.common.model.BaseEntity;
import amu.zhcet.data.attendance.Attendance;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.user.student.Student;
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
    @ManyToOne(fetch = FetchType.LAZY)
    private FloatedCourse floatedCourse;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    private char mode;

    @Valid
    @NotNull
    @PrimaryKeyJoinColumn
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

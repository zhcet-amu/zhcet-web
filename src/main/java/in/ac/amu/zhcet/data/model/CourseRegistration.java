package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import in.ac.amu.zhcet.data.model.base.key.SessionStudent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@IdClass(SessionStudent.class)
public class CourseRegistration extends BaseEntity implements Serializable {
    @Id
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumns(value = {
            @JoinColumn(name = "COURSE_CODE", updatable = false, insertable = false),
            @JoinColumn(name = "SESSION", updatable = false, insertable = false)
    })
    private FloatedCourse floatedCourse;
    @Id
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "STUDENT_ID", updatable = false, insertable = false)
    private Student student;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER, mappedBy = "courseRegistration")
    private Attendance attendance = new Attendance();

    public CourseRegistration(Student student, FloatedCourse floatedCourse) {
        this.student = student;
        this.floatedCourse = floatedCourse;
    }

    @PrePersist
    public void setRelation() {
        attendance.setCourseRegistration(this);
    }
}

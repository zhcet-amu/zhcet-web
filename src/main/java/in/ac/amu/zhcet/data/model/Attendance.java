package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.entity.BaseIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"COURSE_CODE", "SESSION", "STUDENT_ID"})
})
public class Attendance extends BaseIdEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumns(value = {
            @JoinColumn(name = "COURSE_CODE"),
            @JoinColumn(name = "SESSION"),
            @JoinColumn(name = "STUDENT_ID")
    })
    private CourseRegistration courseRegistration;
    private int delivered;
    private int attended;

    public Attendance(CourseRegistration courseRegistration) {
        this.courseRegistration = courseRegistration;
    }

    @Override
    public String toString() {
        String student = courseRegistration != null ? courseRegistration.getStudent().toString() : "";
        String course = courseRegistration != null ? courseRegistration.getFloatedCourse().toString() : "";

        return "Attendance{" +
                "student=" + student +
                ", course=" + course +
                ", delivered=" + delivered +
                ", attended=" + attended +
                '}';
    }
}

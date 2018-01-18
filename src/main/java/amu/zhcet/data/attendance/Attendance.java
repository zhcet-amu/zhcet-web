package amu.zhcet.data.attendance;

import amu.zhcet.common.model.BaseEntity;
import amu.zhcet.data.course.registration.CourseRegistration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Attendance extends BaseEntity {

    @Id
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private CourseRegistration courseRegistration;
    private int delivered;
    private int attended;

    public Attendance(CourseRegistration courseRegistration) {
        this.courseRegistration = courseRegistration;
    }

    public Attendance(CourseRegistration courseRegistration, int delivered, int attended) {
        this.courseRegistration = courseRegistration;
        this.delivered = delivered;
        this.attended = attended;
    }

    @Override
    public String toString() {
        if (courseRegistration == null)
            return "";
        return "Attendance{" +
                "student=" + courseRegistration.getStudent() +
                ", course=" + courseRegistration.getFloatedCourse() +
                ", delivered=" + delivered +
                ", attended=" + attended +
                '}';
    }
}

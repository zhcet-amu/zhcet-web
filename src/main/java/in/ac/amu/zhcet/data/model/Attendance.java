package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Attendance extends BaseEntity {

    @Id
    private String id;

    @OneToOne
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

        return "Attendance{" +
                "student=" + courseRegistration.getStudent() +
                ", course=" + courseRegistration.getFloatedCourse() +
                ", delivered=" + delivered +
                ", attended=" + attended +
                '}';
    }
}

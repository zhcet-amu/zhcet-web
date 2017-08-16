package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import in.ac.amu.zhcet.data.model.dto.AttendanceUpload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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

    @PrePersist
    public void setRelation() {
        id = courseRegistration.getId();
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

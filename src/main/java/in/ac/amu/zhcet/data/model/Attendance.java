package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
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
public class Attendance extends BaseEntity implements Serializable {

    @Id
    private String id;

    @NaturalId
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    private CourseRegistration courseRegistration;
    private int delivered;
    private int attended;

    public Attendance(CourseRegistration courseRegistration) {
        this.courseRegistration = courseRegistration;
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

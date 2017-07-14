package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.entity.BaseIdEntity;
import in.ac.amu.zhcet.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints=
@UniqueConstraint(columnNames={"course_id", "session"}))
public class FloatedCourse extends BaseIdEntity {
    @ManyToOne
    private Course course;
    private String session;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<FacultyMember> inCharge;

    public FloatedCourse(String session, Course course, Department department) {
        this.session = session;
        this.course = course;
    }

    @PrePersist
    public void setDefaults() {
        if (session == null)
            session = Utils.getCurrentSession();
    }
}

package in.ac.amu.zhcet.data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Attendance extends BaseIdEntity {
    @ManyToOne
    private Course course;
    @ManyToOne
    private Student student;
    private String session;
    private int delivered;
    private int attended;
}

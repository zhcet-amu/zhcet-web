package in.ac.amu.zhcet.data.model.base.key;

import in.ac.amu.zhcet.data.model.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Session implements Serializable {
    @ManyToOne
    private Course course;
    private String session;
}

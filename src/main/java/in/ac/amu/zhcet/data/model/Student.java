package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.BaseEntity;
import in.ac.amu.zhcet.data.model.base.BaseUser;
import in.ac.amu.zhcet.data.model.base.UserDetails;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;

@Entity
@DynamicUpdate
@SelectBeforeUpdate
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Student extends BaseEntity {

    @Id
    private String enrolmentNumber;

    @Column(unique = true)
    private String facultyNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private BaseUser user;

    @Embedded
    private UserDetails userDetails;

    public Student(BaseUser user, String facultyNumber) {
        this.enrolmentNumber = user.getUserId();
        this.user = user;
        setFacultyNumber(facultyNumber);
    }
}

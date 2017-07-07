package in.ac.amu.zhcet.data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
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

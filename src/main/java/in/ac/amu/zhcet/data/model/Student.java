package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.model.base.user.CustomPrincipal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Student extends CustomPrincipal {
    @Id
    private String enrolmentNumber;

    @Column(unique = true)
    private String facultyNumber;

    public Student(UserAuth user, String facultyNumber) {
        super(user);
        this.enrolmentNumber = user.getUserId();
        setFacultyNumber(facultyNumber);
    }
}

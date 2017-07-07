package in.ac.amu.zhcet.data.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Student {

    @Id
    private final Long id;

    @Column(unique = true)
    private String enrolmentNo;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private BaseUser user;

    protected Student() {
        id = null;
    }

    public Student(BaseUser user, String enrolmentNo) {
        this.id = user.getId();
        this.user = user;
        setEnrolmentNo(enrolmentNo);
    }
}

package in.ac.amu.zhcet.data.model;

import javax.persistence.*;

@Entity
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

    public String getEnrolmentNo() {
        return enrolmentNo;
    }

    public void setEnrolmentNo(String enrolmentNo) {
        this.enrolmentNo = enrolmentNo;
    }

    public Long getId() {
        return id;
    }

    public BaseUser getUser() {
        return user;
    }

    public void setUser(BaseUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", enrolmentNo='" + enrolmentNo + '\'' +
                ", user=" + user +
                '}';
    }
}

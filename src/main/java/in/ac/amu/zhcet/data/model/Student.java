package in.ac.amu.zhcet.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Student extends User {

    @Column(unique = true)
    private String enrolmentNo;

    public Student() {
        super();
    }

    public Student(String userId, String password, String name, String enrolmentNo) {
        super(userId, password, name, new String[]{ "ROLE_STUDENT" });
        setEnrolmentNo(enrolmentNo);
    }

    public String getEnrolmentNo() {
        return enrolmentNo;
    }

    public void setEnrolmentNo(String enrolmentNo) {
        this.enrolmentNo = enrolmentNo;
    }

    @Override
    public String toString() {
        return "Student{" +
                super.toString() +
                "enrolmentNo='" + enrolmentNo + '\'' +
                '}';
    }
}

package in.ac.amu.zhcet.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Student extends User {

    @Column(unique = true)
    private String enrolmentNo;

    @OneToMany
    private List<Attendance> attendances;

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

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    @Override
    public String toString() {
        return "Student{" +
                super.toString() +
                "enrolmentNo='" + enrolmentNo + '\'' +
                ", attendances=" + attendances +
                '}';
    }
}

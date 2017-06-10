package in.ac.amu.zhcet.data.model;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class Student extends User {

    private String enrolmentNo;

    private List<Attendance> attendances;


    public Student(){}
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

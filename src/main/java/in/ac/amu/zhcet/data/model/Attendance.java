package in.ac.amu.zhcet.data.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Attendance extends BaseEntity {
    @ManyToOne
    private Course course;
    @ManyToOne
    private Student student;
    private String session;
    private int delivered;
    private int attended;

    public Attendance() {
        super();
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getDelivered() {
        return delivered;
    }

    public void setDelivered(int delivered) {
        this.delivered = delivered;
    }

    public int getAttended() {
        return attended;
    }

    public void setAttended(int attended) {
        this.attended = attended;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "course=" + course +
                ", delivered=" + delivered +
                ", attended=" + attended +
                '}';
    }
}

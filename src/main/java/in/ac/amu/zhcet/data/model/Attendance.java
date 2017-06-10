package in.ac.amu.zhcet.data.model;

public class Attendance {

    private Course course;
    private int delivered;
    private int attended;

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

    @Override
    public String toString() {
        return "Attendance{" +
                "course=" + course +
                ", delivered=" + delivered +
                ", attended=" + attended +
                '}';
    }
}

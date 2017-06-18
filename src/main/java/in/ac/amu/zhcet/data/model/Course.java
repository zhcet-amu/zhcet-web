package in.ac.amu.zhcet.data.model;

import javax.persistence.*;

@Entity
public class Course extends BaseEntity {

    private String title;
    @Column(unique = true)
    private String code;

    @ManyToOne
    private Department department;

    public Course() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Course{" +
                "title='" + title + '\'' +
                ", code='" + code + '\'' +
                "department=" + department + '\'' +
                '}';
    }
}

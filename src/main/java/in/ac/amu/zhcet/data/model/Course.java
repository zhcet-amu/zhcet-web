package in.ac.amu.zhcet.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Course extends BaseEntity {

    private String title;
    @Column(unique = true)
    private String code;

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

    @Override
    public String toString() {
        return "Course{" +
                "title='" + title + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}

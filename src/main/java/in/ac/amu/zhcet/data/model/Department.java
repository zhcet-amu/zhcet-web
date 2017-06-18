package in.ac.amu.zhcet.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Department extends BaseEntity {

    private String departmentName;

    public Department() {
        super();
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return "{ " + departmentName + " }";
    }
}

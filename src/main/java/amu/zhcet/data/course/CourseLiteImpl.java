package amu.zhcet.data.course;

import amu.zhcet.data.department.DepartmentLiteImpl;
import lombok.Data;

@Data
public class CourseLiteImpl implements CourseLite {
    private String code;
    private String title;
    private boolean active;
    private DepartmentLiteImpl department;
}

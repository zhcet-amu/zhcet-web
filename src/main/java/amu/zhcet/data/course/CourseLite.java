package amu.zhcet.data.course;

import amu.zhcet.data.department.DepartmentLite;

public interface CourseLite {
    String getCode();
    String getTitle();
    boolean isActive();
    DepartmentLite getDepartment();
}

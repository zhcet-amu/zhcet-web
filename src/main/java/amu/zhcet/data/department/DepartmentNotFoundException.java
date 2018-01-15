package amu.zhcet.data.department;

import amu.zhcet.data.ItemNotFoundException;

import javax.annotation.Nullable;

public class DepartmentNotFoundException extends ItemNotFoundException {

    public DepartmentNotFoundException() {
        super("Department");
    }

    public DepartmentNotFoundException(@Nullable String id) {
        super("Department", id);
    }

}

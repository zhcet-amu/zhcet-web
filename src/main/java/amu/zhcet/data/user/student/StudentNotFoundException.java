package amu.zhcet.data.user.student;

import amu.zhcet.data.ItemNotFoundException;

import javax.annotation.Nullable;

public class StudentNotFoundException extends ItemNotFoundException {
    public StudentNotFoundException() {
        super("Student");
    }

    public StudentNotFoundException(@Nullable String id) {
        super("Student", id);
    }
}

package amu.zhcet.data.course;

import amu.zhcet.data.ItemNotFoundException;

import javax.annotation.Nullable;

public class CourseNotFoundException extends ItemNotFoundException {
    public CourseNotFoundException() {
        super("Course");
    }

    public CourseNotFoundException(@Nullable String id) {
        super("Course", id);
    }
}

package amu.zhcet.data.course.floated;

import amu.zhcet.data.ItemNotFoundException;

import javax.annotation.Nullable;

public class FloatedCourseNotFoundException extends ItemNotFoundException {

    public FloatedCourseNotFoundException() {
        super("Floated Course");
    }

    public FloatedCourseNotFoundException(@Nullable String id) {
        super("Floated Course", id);
    }

}

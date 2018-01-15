package amu.zhcet.data.course.incharge;

import amu.zhcet.data.ItemNotFoundException;

import javax.annotation.Nullable;

public class CourseInChargeNotFoundException extends ItemNotFoundException {
    public CourseInChargeNotFoundException() {
        super("Course In-Charge");
    }

    public CourseInChargeNotFoundException(@Nullable String id) {
        super("Course In-Charge", id);
    }
}

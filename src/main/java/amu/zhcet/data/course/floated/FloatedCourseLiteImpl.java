package amu.zhcet.data.course.floated;

import amu.zhcet.data.course.CourseLiteImpl;
import lombok.Data;

@Data
public class FloatedCourseLiteImpl implements FloatedCourseLite {
    private String session;
    private CourseLiteImpl course;
}

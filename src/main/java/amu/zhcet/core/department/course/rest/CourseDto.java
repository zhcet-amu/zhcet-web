package amu.zhcet.core.department.course.rest;

import lombok.Data;

@Data
class CourseDto {
    private String code;
    private String title;
    private Integer semester;
    private String category;
    private Float credits;
    private boolean floated;
}
package amu.zhcet.data.course;

import amu.zhcet.common.model.BaseEntity;
import amu.zhcet.data.department.Department;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = true)
public class Course extends BaseEntity {

    @Id
    @NotBlank
    @Size(max = 255)
    private String code;
    @NotBlank
    @Size(max = 255)
    private String title;
    private boolean active = true;

    @ManyToOne
    @NotNull
    private Department department;

    @Size(max = 255)
    private String category;
    private Boolean compulsory = true;
    private Integer semester;
    private Integer startYear;
    private Integer finishYear;
    private Float credits;
    @Size(max = 255)
    private String branch;
    @Enumerated(EnumType.STRING)
    private CourseType type;
    private Integer classWorkMarks;
    private Integer midSemMarks;
    private Integer finalMarks;
    private Integer totalMarks;
    private Integer lecturePart;
    private Integer tutorialPart;
    private Integer practicalPart;

    @Size(max = 255)
    private String books;
    @Size(max = 2500)
    @Type(type = "text")
    private String courseObjectives;
    @Size(max = 2500)
    @Type(type = "text")
    private String courseOutcomes;
    @Size(max = 2500)
    @Type(type = "text")
    private String syllabus;
    @Size(max = 2500)
    @Type(type = "text")
    private String description;
}

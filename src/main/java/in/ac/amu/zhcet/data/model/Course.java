package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.CourseType;
import in.ac.amu.zhcet.data.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
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
    private Integer theoryPart;
    private Integer practicalPart;

    @Type(type = "text")
    private String description;
}

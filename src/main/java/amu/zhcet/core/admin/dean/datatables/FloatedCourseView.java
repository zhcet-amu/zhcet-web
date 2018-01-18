package amu.zhcet.core.admin.dean.datatables;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

@Data
class FloatedCourseView {

    @JsonProperty("course_code")
    @JsonView(DataTablesOutput.View.class)
    private String courseCode;
    @JsonProperty("course_title")
    @JsonView(DataTablesOutput.View.class)
    private String courseTitle;
    @JsonView(DataTablesOutput.View.class)
    private String session;
    @JsonProperty("course_department_name")
    @JsonView(DataTablesOutput.View.class)
    private String courseDepartmentName;
    @JsonProperty("course_category")
    @JsonView(DataTablesOutput.View.class)
    private String courseCategory;
    @JsonProperty("course_semester")
    @JsonView(DataTablesOutput.View.class)
    private String courseSemester;
    @JsonProperty("course_branch")
    @JsonView(DataTablesOutput.View.class)
    private String courseBranch;
    @JsonProperty("course_type")
    @JsonView(DataTablesOutput.View.class)
    private String courseType;
    @JsonProperty("course_credits")
    @JsonView(DataTablesOutput.View.class)
    private Float courseCredits;
    @JsonProperty("course_description")
    @JsonView(DataTablesOutput.View.class)
    private String courseDescription;
    @JsonProperty("num_students")
    @JsonView(DataTablesOutput.View.class)
    private int numStudents;
    @JsonView(DataTablesOutput.View.class)
    private String sections;
    @JsonView(DataTablesOutput.View.class)
    private String createdAt;
    @JsonView(DataTablesOutput.View.class)
    private String modifiedAt;
    @JsonView(DataTablesOutput.View.class)
    private String createdBy;
    @JsonView(DataTablesOutput.View.class)
    private String modifiedBy;
    @JsonView(DataTablesOutput.View.class)
    private String version;

}

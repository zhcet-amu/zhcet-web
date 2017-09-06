package in.ac.amu.zhcet.data.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

@Data
public class StudentView {

    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("faculty-number")
    private String facultyNumber;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("enrolment-number")
    private String enrolmentNumber;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("name")
    private String userName;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("email")
    private String userEmail;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("avatar-url")
    private String userDetailsAvatarUrl;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("department")
    private String userDetailsDepartmentName;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("is-verified")
    private boolean userActive;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("created-at")
    private String createdAt;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("modified-at")
    private String modifiedAt;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("created-by")
    private String createdBy;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("modified-by")
    private String modifiedBy;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("version")
    private String version;

}

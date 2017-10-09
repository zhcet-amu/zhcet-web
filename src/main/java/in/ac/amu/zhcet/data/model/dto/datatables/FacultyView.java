package in.ac.amu.zhcet.data.model.dto.datatables;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

@Data
public class FacultyView {

    @JsonView(DataTablesOutput.View.class)
    private String facultyId;
    @JsonView(DataTablesOutput.View.class)
    private String designation;
    @JsonView(DataTablesOutput.View.class)
    private boolean working;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("user_name")
    private String userName;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("user_email")
    private String userEmail;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("avatar-url")
    private String userDetailsAvatarUrl;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("user_department_name")
    private String userDepartmentName;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("is-verified")
    private boolean userActive;
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

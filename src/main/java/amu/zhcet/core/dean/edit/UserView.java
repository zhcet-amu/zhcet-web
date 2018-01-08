package amu.zhcet.core.dean.edit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

@Data
public class UserView {
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("user_name")
    private String userName;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("user_details_gender")
    private String userDetailsGender;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("user_email")
    private String userEmail;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("avatar-url")
    private String userDetailsAvatarUrl;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("original-avatar-url")
    private String userDetailsOriginalAvatarUrl;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("user_department_name")
    private String userDepartmentName;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("is-verified")
    private boolean userEmailVerified;
    @JsonView(DataTablesOutput.View.class)
    @JsonProperty("user_enabled")
    private String userEnabled;
}

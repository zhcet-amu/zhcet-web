package in.ac.amu.zhcet.data.model.dto.datatables;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class FacultyEditModel {
    @NotBlank
    private String userName;
    private String userEmail;
    @NotBlank
    private String userDepartmentName;
    private String userDetailsPhoneNumbers;
    private boolean working;
    private String designation;
}

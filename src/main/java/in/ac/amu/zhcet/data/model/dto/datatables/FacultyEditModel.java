package in.ac.amu.zhcet.data.model.dto.datatables;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

@Data
public class FacultyEditModel {
    @NotBlank
    @Size(max = 255)
    private String userName;
    @Size(max = 255)
    private String userEmail;
    @NotBlank
    private String userDepartmentName;
    @Size(max = 255)
    private String userDetailsPhoneNumbers;
    private boolean working;
    @Size(max = 255)
    private String designation;
}

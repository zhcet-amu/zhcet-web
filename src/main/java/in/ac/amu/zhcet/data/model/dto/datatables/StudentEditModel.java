package in.ac.amu.zhcet.data.model.dto.datatables;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class StudentEditModel {
    @NotBlank
    private String facultyNumber;
    @NotBlank
    private String userName;
    private String userEmail;
    @NotBlank
    private String userDepartmentName;
    private String userDetailsPhoneNumbers;
    private String hallCode;
    private String section;
    private Character status;
}

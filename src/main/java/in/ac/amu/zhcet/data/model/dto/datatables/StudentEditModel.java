package in.ac.amu.zhcet.data.model.dto.datatables;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

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
    @Size(max = 2)
    private String hallCode;
    private String section;
    private Character status;
}

package in.ac.amu.zhcet.data.model.dto.datatables;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

@Data
public class StudentEditModel {
    @NotBlank
    @Size(max = 255)
    private String facultyNumber;
    @NotBlank
    @Size(max = 255)
    private String userName;
    @Email
    @Size(max = 255)
    private String userEmail;
    @NotBlank
    private String userDepartmentName;
    @Size(max = 255)
    private String userDetailsPhoneNumbers;
    @Size(max = 2)
    private String hallCode;
    @Size(max = 255)
    private String section;
    private Character status;
}

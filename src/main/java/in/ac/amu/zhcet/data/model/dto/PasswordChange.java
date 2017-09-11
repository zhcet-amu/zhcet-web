package in.ac.amu.zhcet.data.model.dto;

import lombok.Data;

@Data
public class PasswordChange {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}

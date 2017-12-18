package in.ac.amu.zhcet.data.model.dto;

import lombok.Data;

@Data
public class PasswordReset {
    private String hash;
    private String token;

    private String newPassword;
    private String confirmPassword;
}

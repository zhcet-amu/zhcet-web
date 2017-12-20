package in.ac.amu.zhcet.service.security.password;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordReset extends PasswordChange {
    @NotBlank
    private String hash;
    @NotBlank
    private String token;
}
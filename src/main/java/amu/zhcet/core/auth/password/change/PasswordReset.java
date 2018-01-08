package amu.zhcet.core.auth.password.change;

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
package amu.zhcet.auth.password;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordReset extends PasswordConfirm {
    @NotBlank
    private String hash;
    @NotBlank
    private String token;
}
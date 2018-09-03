package amu.zhcet.auth.verification;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DuplicateEmailException extends RuntimeException {

    private String email;

    public DuplicateEmailException(String email) {
        super("'" + email + "' is already registered by another user");
        this.email = email;
    }

}

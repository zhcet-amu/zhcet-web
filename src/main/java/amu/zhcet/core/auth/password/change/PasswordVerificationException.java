package amu.zhcet.core.auth.password.change;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class PasswordVerificationException extends IllegalStateException {

    private final List<String> errors = new ArrayList<>();

    PasswordVerificationException(List<String> errors) {
        super(errors.toString());
        this.errors.addAll(errors);
    }

    PasswordVerificationException(String error) {
        super(error);
        this.errors.add(error);
    }

}

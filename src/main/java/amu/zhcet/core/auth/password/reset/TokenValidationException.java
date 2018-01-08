package amu.zhcet.core.auth.password.reset;

public class TokenValidationException extends IllegalStateException {

    TokenValidationException(String message) {
        super(message);
    }

}

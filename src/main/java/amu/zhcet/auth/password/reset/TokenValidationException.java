package amu.zhcet.auth.password.reset;

class TokenValidationException extends IllegalStateException {

    TokenValidationException(String message) {
        super(message);
    }

}

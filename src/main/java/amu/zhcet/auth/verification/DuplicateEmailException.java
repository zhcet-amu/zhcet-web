package amu.zhcet.auth.verification;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("'" + email + "' is already registered by another user");
    }

}

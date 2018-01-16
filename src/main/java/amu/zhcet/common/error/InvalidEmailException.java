package amu.zhcet.common.error;

public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String email) {
        super(email + " is an invalid email address");
    }

}

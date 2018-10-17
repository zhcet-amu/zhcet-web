package amu.zhcet.firebase.messaging;

import com.google.api.client.http.HttpResponseException;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.AllArgsConstructor;
import lombok.Data;

public class FirebaseErrorParsingUtils {

    @Data
    @AllArgsConstructor
    public static class TokenStatus {
        private boolean valid;
        private Reason reason;
    }

    @Data
    @AllArgsConstructor
    public static class Reason {
        private int statusCode;
        private String message;
        private String json;
    }

    public static class InvalidThrowableException extends IllegalArgumentException {
        private InvalidThrowableException(Throwable cause) {
            super("Throwable is invalid. Should be FirebaseMessagingException", cause);
        }
    }

    /**
     * Checks if a firebase token is invalid by checking if it either expired or not forbidden
     * @param throwable Throwable of firebase execution error
     * @return TokenStatus denoting status of the token
     */
    public static TokenStatus getTokenStatus(Throwable throwable) {
        Throwable current = throwable;
        while (!(current instanceof FirebaseMessagingException) && current != null) {
            current = current.getCause();
        }

        if (current == null)
            throw new InvalidThrowableException(throwable);

        // We have a FirebaseMessagingException

        FirebaseMessagingException firebaseMessagingException = (FirebaseMessagingException) current;

        while (!(current instanceof HttpResponseException) && current != null) {
            current = current.getCause();
        }

        if (current == null)
            throw new InvalidThrowableException(throwable);

        // We have a HttpResponseException

        HttpResponseException httpResponseException = (HttpResponseException) current;
        int statusCode = httpResponseException.getStatusCode();

        Reason reason = new Reason(statusCode, current.getMessage(), httpResponseException.getContent());

        boolean isTokenExpired = statusCode == 404 || statusCode == 400;
        boolean isTokenForbidden = statusCode == 403;

        if (isTokenExpired || isTokenForbidden) {
            return new TokenStatus(false, reason);
        } else {
            return new TokenStatus(true, reason);
        }
    }

}

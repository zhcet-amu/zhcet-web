package com.google.firebase.messaging;

import amu.zhcet.firebase.messaging.FirebaseErrorParsingUtils;
import amu.zhcet.firebase.messaging.FirebaseMessagingService;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FirebaseMessagingServiceTest {

    private Throwable genericException = new FirebaseMessagingException("OK", "Nice", new RuntimeException());

    private HttpResponseException getResponseError(int code) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return new HttpResponseException.Builder(code, "", httpHeaders)
                .build();
    }

    private boolean isTokenInvalid(@Nullable Throwable throwable) {
        try {
            return !FirebaseErrorParsingUtils.getTokenStatus(throwable).isValid();
        } catch (FirebaseErrorParsingUtils.InvalidThrowableException ite) {
            return false;
        }
    }

    @Test
    public void testNullException() {
        assertFalse(isTokenInvalid(null));
    }

    @Test
    public void testGenericFirebaseException() {
        assertFalse(isTokenInvalid(genericException));
    }

    @Test
    public void testNestedGenericFirebaseException() {
        assertFalse(isTokenInvalid(new ExecutionException(genericException)));
    }

    @Test
    public void testHttpException() {
        assertFalse(isTokenInvalid(getResponseError(404)));
    }

    @Test
    public void testHttpExceptionTrue() {
        assertTrue(isTokenInvalid(new FirebaseMessagingException("OK", "Good", getResponseError(404))));
    }

    @Test
    public void testHttpExceptionTrue400() {
        assertTrue(isTokenInvalid(new FirebaseMessagingException("OK", "Good", getResponseError(404))));
    }

    @Test
    public void testHttpExceptionNestedTrue400() {
        assertTrue(isTokenInvalid(new ExecutionException(new FirebaseMessagingException("OK", "Good", getResponseError(400)))));
    }

    @Test
    public void testHttpExceptionDifferentCode() {
        assertFalse(isTokenInvalid(new FirebaseMessagingException("OK", "Good", getResponseError(500))));
    }

    @Test
    public void testHttpExceptionTrue403() {
        assertTrue(isTokenInvalid(new FirebaseMessagingException("OK", "Good", getResponseError(404))));
    }

    @Test
    public void testHttpExceptionNestedTrue403() {
        assertTrue(isTokenInvalid(new ExecutionException(new FirebaseMessagingException("OK", "Good", getResponseError(403)))));
    }


}
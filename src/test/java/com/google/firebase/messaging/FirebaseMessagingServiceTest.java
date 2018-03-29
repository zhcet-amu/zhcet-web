package com.google.firebase.messaging;

import amu.zhcet.firebase.messaging.FirebaseMessagingService;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import org.junit.Test;

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

    @Test
    public void testNullException() {
        assertFalse(FirebaseMessagingService.isTokenExpired(null));
    }

    @Test
    public void testGenericFirebaseException() {
        assertFalse(FirebaseMessagingService.isTokenExpired(genericException));
    }

    @Test
    public void testNestedGenericFirebaseException() {
        assertFalse(FirebaseMessagingService.isTokenExpired(new ExecutionException(genericException)));
    }

    @Test
    public void testHttpException() {
        assertFalse(FirebaseMessagingService.isTokenExpired(getResponseError(404)));
    }

    @Test
    public void testHttpExceptionTrue() {
        assertTrue(FirebaseMessagingService.isTokenExpired(new FirebaseMessagingException("OK", "Good", getResponseError(404))));
    }

    @Test
    public void testHttpExceptionTrue400() {
        assertTrue(FirebaseMessagingService.isTokenExpired(new FirebaseMessagingException("OK", "Good", getResponseError(404))));
    }

    @Test
    public void testHttpExceptionDifferentCode() {
        assertFalse(FirebaseMessagingService.isTokenExpired(new FirebaseMessagingException("OK", "Good", getResponseError(500))));
    }

    @Test
    public void testHttpExceptionNestedTrue() {
        assertTrue(FirebaseMessagingService.isTokenExpired(new ExecutionException(new FirebaseMessagingException("OK", "Good", getResponseError(404)))));
    }

    @Test
    public void testHttpExceptionNestedTrue400() {
        assertTrue(FirebaseMessagingService.isTokenExpired(new ExecutionException(new FirebaseMessagingException("OK", "Good", getResponseError(400)))));
    }

}
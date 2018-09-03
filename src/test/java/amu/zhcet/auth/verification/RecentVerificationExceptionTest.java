package amu.zhcet.auth.verification;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class RecentVerificationExceptionTest {

    private LocalDateTime localDateTime = LocalDateTime.of(2018, 9, 1, 12, 23);

    @Test
    public void testNoConstructorMessage() {
        RecentVerificationException recentVerificationException = new RecentVerificationException();
        assertEquals(
                "Verification link was recently sent. Please wait for some time",
                recentVerificationException.getMessage());
    }

    @Test
    public void testConstructorMessage() {
        RecentVerificationException recentVerificationException = new RecentVerificationException(localDateTime);
        assertEquals(
                "Verification link was recently sent at 2018-09-01T12:23. Please wait for some time",
                recentVerificationException.getMessage());
    }

    @Test
    public void testGettingSentTime() {
        RecentVerificationException recentVerificationException = new RecentVerificationException(localDateTime);
        assertEquals(localDateTime, recentVerificationException.getSentTime());
    }

}
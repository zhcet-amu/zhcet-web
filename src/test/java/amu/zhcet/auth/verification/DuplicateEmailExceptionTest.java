package amu.zhcet.auth.verification;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DuplicateEmailExceptionTest {

    private DuplicateEmailException duplicateEmailException;

    @Before
    public void setUp() {
        duplicateEmailException = new DuplicateEmailException("apop@gmail.com");
    }

    @Test
    public void testConstructorMessage() {
        assertEquals(
                "'apop@gmail.com' is already registered by another user",
                duplicateEmailException.getMessage());
    }

    @Test
    public void testGettingEmail() {
        assertEquals("apop@gmail.com", duplicateEmailException.getEmail());
    }

}
package amu.zhcet.auth.password;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class PasswordValidatorTest {

    @Test
    public void testPasswordCheckCorrect() {
        PasswordValidator.validatePasswordLength("testpassword");
        PasswordValidator.validatePasswordLength("pultryfarm woth space");
        PasswordValidator.validatePasswordLength("longpasswordigsood");
        PasswordValidator.validatePasswordLength("big new password no");
    }

    private static void assertError(Runnable runnable, String expected) {
        try {
            runnable.run();
            fail("Did not throw exception");
        } catch (PasswordValidationException pve) {
            assertThat(pve.getMessage(), equalTo(expected));
        }
    }

    @Test
    public void testPasswordCheckWrongPasswordLength() {
        String expected = "Passwords should be at least 8 characters long!";

        assertError(() -> PasswordValidator.validatePasswordLength("test"), expected);
        assertError(() -> PasswordValidator.validatePasswordLength("teste"), expected);
        assertError(() -> PasswordValidator.validatePasswordLength("tester"), expected);
        assertError(() -> PasswordValidator.validatePasswordLength(""), expected);
        assertError(() -> PasswordValidator.validatePasswordLength("sevendo"), expected);
    }

    @Test
    public void testPasswordCheckMatching() {
        PasswordValidator.validatePasswordEquality("testing", "testing");
        PasswordValidator.validatePasswordEquality("paratrooper", "paratrooper");
        PasswordValidator.validatePasswordEquality(">.poi 90_+/^", ">.poi 90_+/^");
    }

    @Test
    public void testPasswordCheckWrongNonMatching() {
        String expected = "Passwords don't match";

        assertError(() -> PasswordValidator.validatePasswordEquality("testing", "testnig"), expected);
        assertError(() -> PasswordValidator.validatePasswordEquality("test", "tester"), expected);
        assertError(() -> PasswordValidator.validatePasswordEquality("tester", "tes"), expected);
    }

}
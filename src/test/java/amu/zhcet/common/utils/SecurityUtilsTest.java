package amu.zhcet.common.utils;

import amu.zhcet.core.auth.password.change.PasswordChangeService;
import amu.zhcet.security.SecurityUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;

public class SecurityUtilsTest {

    @Test
    public void testPasswordCheckCorrect() {
        MatcherAssert.assertThat(PasswordChangeService.validatePasswordLength("testpassword", "testpassword"),
                is(emptyCollectionOf(String.class)));
    }

    @Test
    public void testPasswordCheckWrongPasswordLength() {
        String expected = "Passwords should be at least 8 characters long!";

        assertThat(PasswordChangeService.validatePasswordLength("test", "test"),
                hasItem(expected));

        assertThat(PasswordChangeService.validatePasswordLength("test", "tester"),
                hasItem(expected));

        assertThat(PasswordChangeService.validatePasswordLength("teste", "tes"),
                hasItem(expected));
    }

    @Test
    public void testPasswordCheckWrongNonMatching() {
        String expected = "Passwords don't match!";

        assertThat(PasswordChangeService.validatePasswordLength("testing", "testnig"),
                hasItem(expected));

        assertThat(PasswordChangeService.validatePasswordLength("test", "tester"),
                hasItem(expected));

        assertThat(PasswordChangeService.validatePasswordLength("tester", "tes"),
                hasItem(expected));
    }

    @Test
    public void testHashMatches() {
        String email = "jamal.areeb@gmail.com";

        String hash = SecurityUtils.getHash(email);
        assertThat(SecurityUtils.hashMatches(email, hash), equalTo(true));
    }

    @Test
    public void testGeneratePasswordLength() {
        int length = 16;
        String generated = SecurityUtils.generatePassword(length);
        assertThat(generated.length(), equalTo(length));
    }

}

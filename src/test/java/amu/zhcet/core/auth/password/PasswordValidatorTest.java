package amu.zhcet.core.auth.password;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;

public class PasswordValidatorTest {

    @Test
    public void testPasswordCheckCorrect() {
        assertThat(PasswordValidator.validatePasswordLength("testpassword", "testpassword"),
                is(emptyCollectionOf(String.class)));
    }

    @Test
    public void testPasswordCheckWrongPasswordLength() {
        String expected = "Passwords should be at least 8 characters long!";

        assertThat(PasswordValidator.validatePasswordLength("test", "test"),
                hasItem(expected));

        assertThat(PasswordValidator.validatePasswordLength("test", "tester"),
                hasItem(expected));

        assertThat(PasswordValidator.validatePasswordLength("teste", "tes"),
                hasItem(expected));
    }

    @Test
    public void testPasswordCheckWrongNonMatching() {
        String expected = "Passwords don't match!";

        assertThat(PasswordValidator.validatePasswordLength("testing", "testnig"),
                hasItem(expected));

        assertThat(PasswordValidator.validatePasswordLength("test", "tester"),
                hasItem(expected));

        assertThat(PasswordValidator.validatePasswordLength("tester", "tes"),
                hasItem(expected));
    }

}
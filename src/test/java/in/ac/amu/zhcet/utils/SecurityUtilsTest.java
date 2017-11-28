package in.ac.amu.zhcet.utils;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;

public class SecurityUtilsTest {

    @Test
    public void testPasswordCheckCorrect() {
        assertThat(SecurityUtils.validatePassword("testpassword", "testpassword"),
                is(emptyCollectionOf(String.class)));
    }

    @Test
    public void testPasswordCheckWrongPasswordLength() {
        String expected = "Passwords should be at least 6 characters long!";

        assertThat(SecurityUtils.validatePassword("test", "test"),
                hasItem(expected));

        assertThat(SecurityUtils.validatePassword("test", "tester"),
                hasItem(expected));

        assertThat(SecurityUtils.validatePassword("teste", "tes"),
                hasItem(expected));
    }

    @Test
    public void testPasswordCheckWrongNonMatching() {
        String expected = "Passwords don't match!";

        assertThat(SecurityUtils.validatePassword("testing", "testnig"),
                hasItem(expected));

        assertThat(SecurityUtils.validatePassword("test", "tester"),
                hasItem(expected));

        assertThat(SecurityUtils.validatePassword("tester", "tes"),
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

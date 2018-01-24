package amu.zhcet.common.utils;

import amu.zhcet.security.SecurityUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SecurityUtilsTest {

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

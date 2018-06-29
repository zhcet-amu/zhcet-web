package amu.zhcet.common.utils;

import amu.zhcet.security.CryptoUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SecurityUtilsTest {

    @Test
    public void testHashMatches() {
        String email = "jamal.areeb@gmail.com";

        String hash = CryptoUtils.getHash(email);
        assertThat(CryptoUtils.hashMatches(email, hash), equalTo(true));
    }

    @Test
    public void testGeneratePasswordLength() {
        int length = 16;
        String generated = CryptoUtils.generatePassword(length);
        assertThat(generated.length(), equalTo(length));
    }

}

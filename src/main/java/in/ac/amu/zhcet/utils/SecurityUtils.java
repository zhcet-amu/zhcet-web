package in.ac.amu.zhcet.utils;

import com.google.common.hash.Hashing;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.nio.charset.Charset;
import java.security.SecureRandom;

public class SecurityUtils {

    public static String PEPPER = "some_nice_pepper";

    // Prevent instantiation of Util class
    private SecurityUtils() {}

    public static String getHash(String string) {
        return Hashing.sha256()
                .newHasher()
                .putString(PEPPER+string+PEPPER, Charset.defaultCharset())
                .hash()
                .toString();
    }

    public static boolean hashMatches(String string, String hash) {
        return getHash(string).equals(hash);
    }

    public static String generatePassword(int length){
        char[] possibleCharacters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#_-?%&*"
                        .toCharArray();
        return RandomStringUtils.random(length,
                0, possibleCharacters.length - 1,
                false, false,
                possibleCharacters, new SecureRandom());
    }

    public static boolean isRememberMe(Authentication authentication) {
        return authentication != null && authentication.getClass().isAssignableFrom(RememberMeAuthenticationToken.class);
    }

    public static boolean isAnonymous(Authentication authentication) {
        return authentication != null && authentication.getClass().isAssignableFrom(AnonymousAuthenticationToken.class);
    }

    public static boolean isFullyAuthenticated(Authentication authentication) {
        return !(isRememberMe(authentication) || isAnonymous(authentication));
    }

}

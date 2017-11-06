package in.ac.amu.zhcet.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class SecurityUtils {

    public static String SALT = "some_nice_salt";

    // Prevent instantiation of Util class
    private SecurityUtils() {}

    public static List<String> validatePassword(String pass, String repass) {
        List<String> errors = new ArrayList<>();

        if(!pass.equals(repass))
            errors.add("Passwords don't match!");
        if (repass.length() < 6)
            errors.add("Passwords should be at least 6 characters long!");

        return errors;
    }

    public static String getHash(String email) {
        return Hashing.sha256()
                .newHasher()
                .putString(SALT+email+SALT, Charset.defaultCharset())
                .hash()
                .toString();
    }

    public static boolean hashMatches(String email, String hash) {
        return getHash(email).equals(hash);
    }

}

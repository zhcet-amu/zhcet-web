package in.ac.amu.zhcet.utils;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.common.hash.Hashing;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import org.apache.commons.text.WordUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static String SALT = "some_nice_salt";

    public static String getDefaultSessionCode(){
        LocalDate localDate = LocalDate.now();
        int month = localDate.getMonthValue();

        String session = "A";
        if (month >= 1 && month < 6)
            session ="W";

        String year = String.valueOf(localDate.getYear() % 100);
        session += year;

        return session;
    }

    public static String getDefaultSessionName() {
        return getSessionName(getDefaultSessionCode());
    }

    public static String getSessionName(String sessionCode) {
        if (sessionCode.charAt(0) == 'A')
            return "Autumn '" + sessionCode.substring(1);
        else
            return "Winter '" + sessionCode.substring(1);
    }

    public static String capitalizeFirst(String string) {
        if (string == null) return null;
        return WordUtils.capitalizeFully(string.trim());
    }

    public static String capitalizeAll(String string) {
        if (string == null) return null;
        return string.trim().toUpperCase(Locale.getDefault());
    }

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    public static String nullIfEmpty(String string) {
        return defaultString(string, null);
    }

    public static String defaultString(String string, String defaultString) {
        if (isEmpty(string))
            return defaultString;

        return string;
    }

    public static void sortAttendance(List<CourseRegistration> courseRegistrations) {
        courseRegistrations.sort((att1, att2) ->
                ComparisonChain.start()
                        .compare(att1.getStudent().getSection(), att2.getStudent().getSection(), Ordering.natural().nullsFirst())
                        .compare(att1.getStudent().getFacultyNumber().substring(5), att2.getStudent().getFacultyNumber().substring(5))
                        .result()
            );
    }

    public static BufferedImage readImage(MultipartFile file) {
        try {
            return ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            return null;
        }
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

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

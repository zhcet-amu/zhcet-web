package in.ac.amu.zhcet.utils;

import com.google.common.base.Strings;
import org.apache.commons.text.WordUtils;

import java.util.Locale;

public class StringUtils {

    // Prevent instantiation of Util class
    private StringUtils() {}

    public static String capitalizeFirst(String string) {
        if (string == null) return null;
        return WordUtils.capitalizeFully(string.trim());
    }

    public static String capitalizeAll(String string) {
        if (string == null) return null;
        return string.trim().toUpperCase(Locale.getDefault());
    }

    public static String defaultString(String string, String defaultString) {
        if (Strings.isNullOrEmpty(string))
            return defaultString;

        return string;
    }

}

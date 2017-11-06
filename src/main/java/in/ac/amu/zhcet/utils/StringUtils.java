package in.ac.amu.zhcet.utils;

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

}

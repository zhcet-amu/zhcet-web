package in.ac.amu.zhcet.utils;

import com.google.common.base.Strings;
import org.apache.commons.text.WordUtils;

import javax.annotation.Nullable;
import java.util.Locale;

public class StringUtils {

    // Prevent instantiation of Util class
    private StringUtils() {}

    @Nullable
    public static String capitalizeFirst(String string) {
        if (string == null) return null;
        return Strings.emptyToNull(WordUtils.capitalizeFully(string.trim()));
    }

    @Nullable
    public static String capitalizeAll(String string) {
        if (string == null) return null;
        return Strings.emptyToNull(string.trim().toUpperCase(Locale.getDefault()));
    }

    public static String defaultString(String string, String defaultString) {
        if (Strings.isNullOrEmpty(string))
            return defaultString;

        return string;
    }

}

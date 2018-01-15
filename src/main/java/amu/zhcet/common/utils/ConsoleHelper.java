package amu.zhcet.common.utils;

public class ConsoleHelper {

    public static String blue(String string) {
        return color(ConsoleColors.BLUE, string);
    }

    public static String green(String string) {
        return color(ConsoleColors.GREEN, string);
    }

    public static String red(String string) {
        return color(ConsoleColors.RED, string);
    }

    public static String color(String color, String string) {
        return color + string + ConsoleColors.RESET;
    }

}

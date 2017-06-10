package in.ac.amu.zhcet.utils;

import java.time.LocalDate;

public class Utils {
    public static String getCurrentSession(){
        LocalDate localDate = LocalDate.now();
        int month = localDate.getMonthValue();

        String session = "A";
        if (month >= 1 && month < 6)
            session ="W";

        String year = String.valueOf(localDate.getYear() % 100);
        session += year;

        return session;
    }
}

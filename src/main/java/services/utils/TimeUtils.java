package services.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static String getDateNowAsISODateTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return formatter.format(LocalDate.now());
    }

    public static String getDateNowPlus6MonthsAsISODateTimeString() {
        LocalDate plus6months = LocalDate.now().plusMonths(6);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return formatter.format(plus6months);
    }

    public static boolean isISOStringOutOfDate(String isoDateString) {
        LocalDate isoDate = LocalDate.parse(isoDateString, DateTimeFormatter.ISO_DATE_TIME);
        return isoDate.isBefore(LocalDate.now());
    }
}

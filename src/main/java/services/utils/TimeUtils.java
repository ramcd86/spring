package services.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static String getDateNowAsISODateTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return formatter.format(LocalDate.now().atStartOfDay());
    }

    public static String getDateNowPlus6MonthsAsISODateTimeString() {
        LocalDateTime plus6Months = LocalDate.now()
                .plusMonths(6)
                .atStartOfDay();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return formatter.format(plus6Months);
    }

    public static boolean isISOStringOutOfDate(String isoDateString) {
        LocalDate isoDate = LocalDate.parse(isoDateString, DateTimeFormatter.ISO_DATE_TIME);
        LocalDate now = LocalDate.parse(getDateNowAsISODateTimeString(),
                DateTimeFormatter.ISO_DATE_TIME);
        return isoDate.isBefore(now);
    }
}

package fr.travauxetservices.tools;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Phobos on 04/02/15.
 */
public class DateToolkit {
    public static String format(Date date, Locale locale) {
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        if (truncateTime(date).equals(truncateTime(today.getTime()))) {
            return I18N.getString("date.today");
        } else if (truncateTime(date).equals(truncateTime(yesterday.getTime()))) {
            return I18N.getString("date.yesterday");
        }
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        return df.format(date);
    }

    private static Date truncateTime(Date d) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Date(cal.getTime().getTime());
    }
}

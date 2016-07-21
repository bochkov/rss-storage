package com.sergeybochkov.rss.radioutkin.service.worker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorkerCommon {

    public static String normalize(String value) {
        String rezult = value
                .trim()
                .replaceAll("\\\\", "")
                .replace("\r\n", "\r");

        Pattern linkPattern = Pattern.compile("(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w?=.-]*)*/?");
        Matcher matcher = linkPattern.matcher(rezult);
        if (matcher.find())
            rezult = matcher.replaceAll("<a href=\"" + matcher.group() + "\">" + matcher.group() + "</a>");

        return rezult;
    }

    private static final Pattern pattern = Pattern.compile("^(.+) (пишет|отвечает) (\\d+).(\\d+).(\\d+) в (\\d+):(\\d+)$");

    public static String parseAuthor(String value) {
        Matcher matcher = pattern.matcher(value);
        if (matcher.find())
            return matcher.group(1);
        return "";
    }

    public static Date parseDate(String value){
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(matcher.group(3)));
            cal.set(Calendar.MONTH, Integer.parseInt(matcher.group(4)) - 1);
            cal.set(Calendar.YEAR, Integer.parseInt(matcher.group(5)));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher.group(6)));
            cal.set(Calendar.MINUTE, Integer.parseInt(matcher.group(7)));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }

        return null;
    }
}

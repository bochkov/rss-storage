package com.sergeybochkov.rss.radioutkin.source;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractSource implements Source, Runnable {

    private static final Pattern LINK_PATTERN = Pattern.compile("(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w?=.-]*)*/?");
    private static final Pattern AUTHOR_DATE_PATTERN = Pattern.compile("^(.+) (пишет|отвечает) (\\d+).(\\d+).(\\d+) в (\\d+):(\\d+)$");

    public String normalize(String value) {
        String result = value
                .trim()
                .replaceAll("\\\\", "")
                .replace("\r\n", "\r");
        Matcher matcher = LINK_PATTERN.matcher(result);
        return matcher.find() ?
                matcher.replaceAll("<a href=\"" + matcher.group() + "\">" + matcher.group() + "</a>") :
                result;
    }

    public String parseAuthor(String value) {
        Matcher m = AUTHOR_DATE_PATTERN.matcher(value);
        return (m.find()) ?
                m.group(1) :
                "";
    }

    public Date parseDate(String value){
        Matcher matcher = AUTHOR_DATE_PATTERN.matcher(value);
        Calendar cal = Calendar.getInstance();
        if (matcher.find()) {
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(matcher.group(3)));
            cal.set(Calendar.MONTH, Integer.parseInt(matcher.group(4)) - 1);
            cal.set(Calendar.YEAR, Integer.parseInt(matcher.group(5)));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher.group(6)));
            cal.set(Calendar.MINUTE, Integer.parseInt(matcher.group(7)));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        return cal.getTime();
    }

    public abstract Logger log();

    @Override
    public void run() {
        try {
            download();
        }
        catch (IOException ex) {
            log().warn(ex.getMessage(), ex);
        }
    }
}

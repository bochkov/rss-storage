package com.sergeybochkov.rss.lostfilm.parsing;

import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateElement implements SourceElement<Date> {

    private static final DateFormat DF = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static final Pattern PATTERN = Pattern.compile("Дата: (.*)\\..*?Комментариев.*");

    private final Element element;

    public DateElement(Element element) {
        this.element = element;
    }

    @Override
    public Date parse() throws ParseException {
        Matcher m = PATTERN.matcher(
                element.getElementsByClass("micro").get(0).text());
        if (m.find())
            return DF.parse(m.group(1));
        throw new ParseException("Pattern no match", 0);
    }
}

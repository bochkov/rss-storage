package com.sergeybochkov.rss.lostfilm.parsing;

import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateElement implements SourceElement<Date> {

    private static final DateFormat DF = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));

    private final Element element;

    public DateElement(Element element) {
        this.element = element;
    }

    @Override
    public Date parse() throws ParseException {
        return DF.parse(element.text());
    }
}

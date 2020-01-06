package com.sergeybochkov.rss.lostfilm.parsing;

import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateElement implements SourceElement<Date> {

    private final Element element;

    public DateElement(Element element) {
        this.element = element;
    }

    @Override
    public Date parse() throws ParseException {
        return new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"))
                .parse(element.text());
    }
}

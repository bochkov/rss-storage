package com.sergeybochkov.rss.lostfilm.parsing;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ArticleElement implements SourceElement<Integer> {

    private static final Pattern PATTERN = Pattern.compile("/news/id(\\d+)");

    private final String url;

    public ArticleElement(String url) {
        this.url = url;
    }

    @Override
    public Integer parse() throws ParseException {
        Matcher m = PATTERN.matcher(url);
        if (m.find())
            return Integer.parseInt(m.group(1));
        throw new ParseException("No article found in url", 0);
    }
}

package com.sergeybochkov.rss.lostfilm.parsing;

import org.jsoup.nodes.Element;

public final class BodyElement implements SourceElement<String> {

    private static final String[] IGNORED_CLASSES = {
            "zoom-btn", "hor-spacer", "arrow"
    };

    private final Element element;
    private final String baseUrl;

    public BodyElement(Element element, String baseUrl) {
        this.element = element;
        for (String ignore : IGNORED_CLASSES)
            this.element.getElementsByClass(ignore).remove();
        this.baseUrl = baseUrl;
    }

    @Override
    public String parse() {
        // normalize urls
        for (Element link : element.select("a"))
            link.attr("href", String.format("%s%s", baseUrl, link.attr("href")));
        // add bootstrap class to img
        for (Element img : element.select("img"))
            img.addClass("img-responsive");
        return element.html();
    }
}

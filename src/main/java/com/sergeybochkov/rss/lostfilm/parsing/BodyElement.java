package com.sergeybochkov.rss.lostfilm.parsing;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.ParseException;

public final class BodyElement implements SourceElement<String> {

    private final String url;
    private final String userAgent;

    public BodyElement(String url, String userAgent) {
        this.url = url;
        this.userAgent = userAgent;
    }

    @Override
    public String parse() throws ParseException {
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent(userAgent)
                    .execute();
            if (response.statusCode() == 200) {
                Document doc = response.parse();
                Element html = doc.getElementsByClass("content_body").get(0);
                html.getElementsByAttributeValueContaining("style", "display:block").remove();
                return normalizeUrls(html.html()).trim();
            }
            throw new ParseException("Document not found", 1);
        }
        catch (IOException ex) {
            throw new ParseException("Document not found", 0);
        }
    }

    private String normalizeUrls(String html) {
        if (html.contains("src=\"/"))
            html = html.replaceAll("src=\"/", "src=\"" + url + "/");
        return html;
    }
}

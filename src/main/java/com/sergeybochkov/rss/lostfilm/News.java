package com.sergeybochkov.rss.lostfilm;

import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Link;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = News.COLLECTION_NAME)
public final class News implements Serializable {

    public static final String COLLECTION_NAME = "lostfilm_news2";

    @Id
    private String id;
    private Integer articleId;
    private Date date;
    private String title;
    private String text;
    private String url;
    private String imgUrl;

    public Entry toFeedEntry() {
        Entry entry = new Entry();
        entry.setId(String.valueOf(articleId));
        entry.setTitle(title);
        entry.setPublished(date);

        Link link = new Link();
        link.setHref(url);
        entry.setAlternateLinks(Collections.singletonList(link));

        Content content = new Content();
        content.setType("text/html");
        content.setValue(String.format("<img src='%s'/><br/>%s", imgUrl, text));
        entry.setContents(Collections.singletonList(content));

        return entry;
    }

}
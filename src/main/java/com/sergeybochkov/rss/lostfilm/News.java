package com.sergeybochkov.rss.lostfilm;

import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Link;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

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

    public News() {}

    public News(Integer articleId, Date date, String title, String text, String url, String imgUrl) {
        this.articleId = articleId;
        this.date = date;
        this.title = title;
        this.text = text;
        this.url = url;
        this.imgUrl = imgUrl;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
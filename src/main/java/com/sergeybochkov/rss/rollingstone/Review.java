package com.sergeybochkov.rss.rollingstone;

import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.atom.Person;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

@Document(collection = Review.COLLECTION_NAME)
public final class Review implements Serializable {

    public static final String COLLECTION_NAME = "rollingstone";

    @Id
    private String id;
    private String url;
    private String title;
    private String text;
    private String author;
    private Date date;

    public Review() {
    }

    public Review(String id, String url, String title,
                  String text, String author, Date date) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.text = text;
        this.author = author;
        this.date = date;
    }

    public Entry toFeedEntry() {
        Entry entry = new Entry();

        entry.setId(id);
        entry.setTitle(title);
        entry.setPublished(date);

        Content content = new Content();
        content.setValue(text);
        content.setType("text/html");
        entry.setContents(Collections.singletonList(content));

        Link href = new Link();
        href.setHref(url);
        entry.setOtherLinks(Collections.singletonList(href));

        Person person = new Person();
        person.setName(author);
        entry.setAuthors(Collections.singletonList(person));

        return entry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

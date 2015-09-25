package com.sergeybochkov.rss.andronov.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = AndQa.COLLECTION_NAME)
public class AndQa implements Serializable {

    public static final String COLLECTION_NAME = "andronov";

    @Id
    private String id;
    private Date published;
    private String link;
    private String qText;
    private String qAuthor;
    private String aText;
    private String aAuthor;

    public AndQa() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getqText() {
        return qText;
    }

    public void setqText(String qText) {
        this.qText = qText;
    }

    public String getqAuthor() {
        return qAuthor;
    }

    public void setqAuthor(String qAuthor) {
        this.qAuthor = qAuthor;
    }

    public String getaText() {
        return aText;
    }

    public void setaText(String aText) {
        this.aText = aText;
    }

    public String getaAuthor() {
        return aAuthor;
    }

    public void setaAuthor(String aAuthor) {
        this.aAuthor = aAuthor;
    }
}

package com.sergeybochkov.rss.radioutkin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = Qa.COLLECTION_NAME)
public class Qa implements Serializable {

    public static final String COLLECTION_NAME = "radioutkin";

    @Id
    private String id;
    private Date published;
    private Date updated;
    private String link;
    private String q_text;
    private String q_author;
    private String a_text;
    private String a_author;
    private Date timestamp;

    public Qa(){}

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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getQ_text() {
        return q_text;
    }

    public void setQ_text(String q_text) {
        this.q_text = q_text;
    }

    public String getQ_author() {
        return q_author;
    }

    public void setQ_author(String q_author) {
        this.q_author = q_author;
    }

    public String getA_text() {
        return a_text;
    }

    public void setA_text(String a_text) {
        this.a_text = a_text;
    }

    public String getA_author() {
        return a_author;
    }

    public void setA_author(String a_author) {
        this.a_author = a_author;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isPublished() {
        return a_text != null
                && a_author != null
                && updated != null;
    }

    @Override
    public String toString() {
        return "Qa {" +
                "id='" + id + '\'' +
                ", published=" + published +
                ", updated=" + updated +
                ", link='" + link + '\'' +
                ", q_text='" + q_text + '\'' +
                ", q_author='" + q_author + '\'' +
                ", a_text='" + a_text + '\'' +
                ", a_author='" + a_author + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

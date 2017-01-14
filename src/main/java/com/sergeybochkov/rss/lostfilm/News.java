package com.sergeybochkov.rss.lostfilm;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = News.COLLECTION_NAME)
public final class News {

    public static final String COLLECTION_NAME = "lostfilm_news";

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
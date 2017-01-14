package com.sergeybochkov.rss.store;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = Store.COLLECTION_NAME)
public final class Store implements Serializable {

    public static final String COLLECTION_NAME = "store";

    @Id
    private String key;
    private String value;

    public Store() {
    }

    public Store(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

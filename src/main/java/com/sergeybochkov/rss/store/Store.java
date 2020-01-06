package com.sergeybochkov.rss.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = Store.COLLECTION_NAME)
public final class Store implements Serializable {

    public static final String COLLECTION_NAME = "store";

    @Id
    private String key;
    private String value;

}

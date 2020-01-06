package com.sergeybochkov.rss.rollingstone;

import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.atom.Person;
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
}

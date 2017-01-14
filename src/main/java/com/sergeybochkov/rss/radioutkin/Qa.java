package com.sergeybochkov.rss.radioutkin;

import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.atom.Person;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Document(collection = Qa.COLLECTION_NAME)
public final class Qa implements Serializable {

    public static final String COLLECTION_NAME = "radioutkin";
    public static final List<String> MONTHS = Arrays.asList("января", "февраля", "марта",
            "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря");

    private static final DateFormat DF = new SimpleDateFormat("d MMMM yyyy HH:mm", new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return MONTHS.toArray(new String[MONTHS.size()]);
        }
    });

    @Id
    private String id;
    private String link;
    private Date timestamp;
    @Field("q_text")
    private String qText;
    @Field("q_author")
    private String qAuthor;
    private Date published;
    @Field("a_text")
    private String aText;
    @Field("a_author")
    private String aAuthor;
    private Date updated;

    public Qa() {
    }

    public Qa(String id, String link, Date timestamp,
              String qText, String qAuthor, Date published,
              String aText, String aAuthor, Date updated) {
        this.id = id;
        this.link = link;
        this.timestamp = timestamp;
        this.qText = qText;
        this.qAuthor = qAuthor;
        this.published = published;
        this.aText = aText;
        this.aAuthor = aAuthor;
        this.updated = updated;
    }

    public Entry toFeedEntry() {
        Entry entry = new Entry();
        entry.setId(id);
        entry.setPublished(published);
        entry.setUpdated(updated);

        if (link != null) {
            Link href = new Link();
            href.setHref(link);
            entry.setAlternateLinks(Collections.singletonList(href));
        }

        Person qPerson = new Person();
        qPerson.setName(qAuthor);
        Person aPerson = new Person();
        aPerson.setName(aAuthor);
        entry.setAuthors(Arrays.asList(qPerson, aPerson));

        Content content = new Content();
        content.setType("text/html");
        content.setValue(String.format("<p><strong>%s</strong></p><p>%s, <em>%s</em></p><p>%s</p><p>%s, <em>%s</em></p>",
                qText, qAuthor, DF.format(published), aText, aAuthor, updated));
        entry.setContents(Collections.singletonList(content));
        entry.setTitle(String.format("Вопрос от %s", qAuthor));
        return entry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getPublished() {
        return published;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isPublished() {
        return aText != null
                && aAuthor != null
                && updated != null;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Qa qa = (Qa) o;
        if (qText != null ? !qText.equals(qa.qText) : qa.qText != null) return false;
        if (qAuthor != null ? !qAuthor.equals(qa.qAuthor) : qa.qAuthor != null) return false;
        if (aText != null ? !aText.equals(qa.aText) : qa.aText != null) return false;
        return aAuthor != null ? aAuthor.equals(qa.aAuthor) : qa.aAuthor == null;

    }

    @Override
    public int hashCode() {
        int result = qText != null ? qText.hashCode() : 0;
        result = 31 * result + (qAuthor != null ? qAuthor.hashCode() : 0);
        result = 31 * result + (aText != null ? aText.hashCode() : 0);
        result = 31 * result + (aAuthor != null ? aAuthor.hashCode() : 0);
        return result;
    }
}

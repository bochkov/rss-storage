package com.sergeybochkov.rss.radioutkin.web;

import com.sergeybochkov.rss.radioutkin.domain.Qa;
import com.sun.syndication.feed.atom.*;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RssViewer extends AbstractAtomFeedView {

    @Override
    @SuppressWarnings("unchecked")
    protected List<Entry> buildFeedEntries(Map<String, Object> stringObjectMap, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        List<Qa> qas = (List<Qa>)stringObjectMap.get("feed");
        List<Entry> entryList = new ArrayList<>(qas.size());
        for (Qa qa : qas) {
            Entry entry = new Entry();

            entry.setId(qa.getId());
            entry.setPublished(qa.getPublished());
            entry.setUpdated(qa.getUpdated());

            if (qa.getLink() != null) {
                Link link = new Link();
                link.setHref(qa.getLink());
                entry.setAlternateLinks(Arrays.asList(link));
            }

            Person qAuthor = new Person();
            qAuthor.setName(qa.getQ_author());
            Person aAuthor = new Person();
            aAuthor.setName(qa.getA_author());
            entry.setAuthors(Arrays.asList(qAuthor, aAuthor));

            Content content = new Content();
            content.setType("html");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            String html = "<p><strong>" + qa.getQ_text() + "</strong></p>";
            html += "<p>" + qa.getQ_author();
            if (qa.getPublished() != null)
                html += ", <em>" + sdf.format(qa.getPublished()) + "</em>";
            html += "</p>";
            html += "<p>" + qa.getA_text() + "</p>";
            html += "<p>" + qa.getA_author();
            if (qa.getUpdated() != null)
                html += ", <em>" + sdf.format(qa.getUpdated()) + "</em>";
            html += "</p>";

            content.setValue(html);
            entry.setContents(Arrays.asList(content));

            entry.setTitle("Вопрос от " + qa.getQ_author());
            entryList.add(entry);
        }
        return entryList;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
        Qa qa = ((List<Qa>)model.get("feed")).get(0);
        feed.setTitle("Конференция Радио Уткин");
        feed.setUpdated(qa.getUpdated());
        super.buildFeedMetadata(model, feed, request);
    }
}

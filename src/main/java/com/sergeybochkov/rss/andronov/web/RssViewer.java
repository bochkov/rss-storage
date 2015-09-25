package com.sergeybochkov.rss.andronov.web;

import com.rometools.rome.feed.atom.*;
import com.sergeybochkov.rss.andronov.domain.AndQa;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Component(value = "andronov_rss")
public class RssViewer extends AbstractAtomFeedView {

    @Override
    @SuppressWarnings("unchecked")
    protected List<Entry> buildFeedEntries(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        List<AndQa> qas = (List<AndQa>) map.get("feed");
        List<Entry> entryList = new ArrayList<>(qas.size());
        for (AndQa qa : qas) {
            Entry entry = new Entry();

            entry.setId(qa.getId());
            entry.setPublished(qa.getPublished());
            entry.setUpdated(qa.getPublished());

            if (qa.getLink() != null) {
                Link link = new Link();
                link.setHref(qa.getLink());
                entry.setAlternateLinks(Collections.singletonList(link));
            }

            Person qAuthor = new Person();
            qAuthor.setName(qa.getqAuthor());
            Person aAuthor = new Person();
            aAuthor.setName(qa.getaAuthor());
            entry.setAuthors(Arrays.asList(qAuthor, aAuthor));

            Content content = new Content();
            content.setType("html");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            String html = "<p><strong>" + qa.getqText() + "</strong></p>";
            html += "<p>" + qa.getqAuthor();
            if (qa.getPublished() != null)
                html += ", <em>" + sdf.format(qa.getPublished()) + "</em>";
            html += "</p>";
            html += "<p>" + qa.getaText() + "</p>";
            html += "<p>" + qa.getaAuthor();
            html += ", <em>" + sdf.format(qa.getPublished()) + "</em>";
            html += "</p>";

            content.setValue(html);
            entry.setContents(Collections.singletonList(content));

            entry.setTitle("Вопрос от " + qa.getqAuthor());
            entryList.add(entry);
        }
        return entryList;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
        AndQa qa = ((List<AndQa>)model.get("feed")).get(0);
        feed.setTitle("Конференция Радио Уткин");
        feed.setUpdated(qa.getPublished());
        super.buildFeedMetadata(model, feed, request);
    }
}

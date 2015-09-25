package com.sergeybochkov.rss.rollingstone.web;

import com.rometools.rome.feed.atom.*;
import com.sergeybochkov.rss.rollingstone.domain.Review;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component(value = "rs_rss")
public class RssViewer extends AbstractAtomFeedView {
    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
        feed.setTitle("Рецензии RollingStone.ru");
        super.buildFeedMetadata(model, feed, request);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<Entry> buildFeedEntries(Map<String, Object> stringObjectMap, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        List<Review> reviewList = (List<Review>) stringObjectMap.get("feed");
        List<Entry> entryList = new ArrayList<>(reviewList.size());
        for (Review review : reviewList) {
            Entry entry = new Entry();

            entry.setId(review.getId());
            entry.setTitle(review.getTitle());
            entry.setPublished(review.getDate());

            Content content = new Content();
            content.setValue(review.getText());
            content.setType("html");
            entry.setContents(Collections.singletonList(content));

            Link link = new Link();
            link.setHref(review.getUrl());
            entry.setOtherLinks(Collections.singletonList(link));

            Person person = new Person();
            person.setName(review.getAuthor());
            entry.setAuthors(Collections.singletonList(person));

            entryList.add(entry);
        }
        return entryList;
    }
}

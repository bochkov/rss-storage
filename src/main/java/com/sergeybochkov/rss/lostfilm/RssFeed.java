package com.sergeybochkov.rss.lostfilm;

import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component(value = "lostfilm_rss")
public final class RssFeed extends AbstractAtomFeedView {

    @Override
    @SuppressWarnings("unchecked")
    protected List<Entry> buildFeedEntries(Map<String, Object> stringObjectMap, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        List<News> newsList = (List<News>)stringObjectMap.get("feed");
        List<Entry> entryList = new ArrayList<>(newsList.size());
        for (News news : newsList) {
            Entry entry = new Entry();
            entry.setId(String.valueOf(news.getArticleId()));
            entry.setPublished(news.getDate());

            Link link = new Link();
            link.setHref(news.getUrl());
            entry.setAlternateLinks(Collections.singletonList(link));

            Content content = new Content();
            content.setType("text/html");
            String value = "<img src='" + news.getImgUrl() + "'/><br/>" + news.getText();
            content.setValue(value);
            entry.setContents(Collections.singletonList(content));

            entry.setTitle(news.getTitle());
            entryList.add(entry);
        }
        return entryList;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
        News news = ((List<News>) model.get("feed")).get(0);
        feed.setTitle("Новости сериалов от LostFilm.TV");
        feed.setUpdated(news.getDate());
        super.buildFeedMetadata(model, feed, request);
    }
}

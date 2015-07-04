package com.sergeybochkov.rss.itstudent.web;

import com.rometools.rome.feed.atom.*;
import com.sergeybochkov.rss.itstudent.domain.Post;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component(value = "itstudents_rss")
public class RssViewer extends AbstractAtomFeedView {

    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
        feed.setTitle("It-Students :: Новое на сайте");
        super.buildFeedMetadata(model, feed, request);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<Entry> buildFeedEntries(Map<String, Object> stringObjectMap, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        List<Post> postList = (List<Post>) stringObjectMap.get("feed");
        List<Entry> entryList = new ArrayList<>(postList.size());
        for (Post post : postList) {
            Entry entry = new Entry();

            entry.setId(post.getId());
            entry.setTitle(post.getTitle());

            Content content = new Content();
            content.setValue(post.getText());
            content.setType("html");
            entry.setContents(Arrays.asList(content));

            Link link = new Link();
            link.setHref(post.getUrl());
            entry.setOtherLinks(Arrays.asList(link));

            entryList.add(entry);
        }
        return entryList;
    }
}

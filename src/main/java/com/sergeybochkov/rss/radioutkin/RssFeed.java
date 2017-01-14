package com.sergeybochkov.rss.radioutkin;

import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(value = "radioutkin_rss")
public final class RssFeed extends AbstractAtomFeedView {

    @Override
    protected List<Entry> buildFeedEntries(Map<String, Object> objectMap,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        List objects = (List) objectMap.get("feed");
        List<Entry> entries = new ArrayList<>(objects.size());
        for (Object obj : objects)
            if (obj instanceof Qa)
                entries.add(((Qa) obj).toFeedEntry());
        return entries;
    }

    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
        feed.setTitle("Конференция Радио Уткин");
        Object latest = ((List) model.get("feed")).get(0);
        if (latest instanceof Qa)
            feed.setUpdated(((Qa) latest).getUpdated());
        super.buildFeedMetadata(model, feed, request);
    }
}

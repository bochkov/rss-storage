package com.sergeybochkov.rss.lostfilm;

import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(value = "lostfilm_rss")
public final class RssFeed extends AbstractAtomFeedView {

    @Override
    protected List<Entry> buildFeedEntries(Map<String, Object> objectMap,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        List<?> objects = (List<?>) objectMap.get("feed");
        List<Entry> entries = new ArrayList<>(objects.size());
        for (Object object : objects)
            if (object instanceof News)
                entries.add(((News) object).toFeedEntry());
        return entries;
    }

    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
        feed.setTitle("Новости сериалов от LostFilm.TV");
        List<?> objects = (List<?>) model.get("feed");
        Object latest = objects.get(0);
        if (latest instanceof News)
            feed.setUpdated(((News) latest).getDate());
        super.buildFeedMetadata(model, feed, request);
    }
}

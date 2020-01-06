package com.sergeybochkov.rss.rollingstone;

import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(value = "rs_rss")
public final class RssFeed extends AbstractAtomFeedView {

    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
        feed.setTitle("Рецензии RollingStone.ru");
        super.buildFeedMetadata(model, feed, request);
    }

    @Override
    protected List<Entry> buildFeedEntries(Map<String, Object> objectMap,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        List<?> objects = (List<?>) objectMap.get("feed");
        List<Entry> entries = new ArrayList<>(objects.size());
        for (Object object : objects) {
            if (object instanceof Review)
                entries.add(((Review) object).toFeedEntry());
        }
        return entries;
    }
}

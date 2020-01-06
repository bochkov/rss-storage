package com.sergeybochkov.rss.lostfilm;

import java.util.List;

public interface NewsDao {

    void save(News news);

    List<News> getLatest();

    boolean notExists(News news);
}

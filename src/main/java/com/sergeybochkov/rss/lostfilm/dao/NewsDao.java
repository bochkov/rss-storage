package com.sergeybochkov.rss.lostfilm.dao;

import com.sergeybochkov.rss.lostfilm.domain.News;

import java.util.List;

public interface NewsDao {

    void save(News news);

    List<News> getLatest();

    boolean exists(News news);
}

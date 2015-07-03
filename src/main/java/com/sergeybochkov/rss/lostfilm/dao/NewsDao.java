package com.sergeybochkov.rss.lostfilm.dao;

import com.sergeybochkov.rss.lostfilm.domain.News;

import java.util.List;

public interface NewsDao {

    public void save(News news);

    public List<News> getLatest();

    public boolean exists(News news);
}

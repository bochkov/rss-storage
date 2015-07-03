package com.sergeybochkov.rss.lostfilm.service;

import com.sergeybochkov.rss.lostfilm.domain.News;

import java.util.List;

public interface NewsService {

    public List<News> getLatest();
}

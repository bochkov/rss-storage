package com.sergeybochkov.rss.rollingstone.dao;

import com.sergeybochkov.rss.rollingstone.domain.Review;

import java.util.List;

public interface ReviewDao {

    public List<Review> getLatest();

    public Review find(String id);

    public void add(Review review);
}

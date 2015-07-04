package com.sergeybochkov.rss.rollingstone.dao;

import com.sergeybochkov.rss.rollingstone.domain.Review;

import java.util.List;

public interface ReviewDao {

    List<Review> getLatest();

    Review find(String id);

    void add(Review review);
}

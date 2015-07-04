package com.sergeybochkov.rss.rollingstone.service;

import com.sergeybochkov.rss.rollingstone.domain.Review;

import java.util.List;

public interface ReviewService {

    List<Review> getLatest();

    Review find(String id);

    void add(Review review);
}

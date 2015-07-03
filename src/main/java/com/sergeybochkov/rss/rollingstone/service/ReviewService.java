package com.sergeybochkov.rss.rollingstone.service;

import com.sergeybochkov.rss.rollingstone.domain.Review;

import java.util.List;

public interface ReviewService {

    public List<Review> getLatest();

    public Review find(String id);

    public void add(Review review);
}

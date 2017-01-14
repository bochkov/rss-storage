package com.sergeybochkov.rss.rollingstone;

import java.util.List;

public interface ReviewDao {

    List<Review> getLatest(int limit);

    Review find(String id);

    void add(Review review);
}

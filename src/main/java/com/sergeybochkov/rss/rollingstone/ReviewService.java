package com.sergeybochkov.rss.rollingstone;

import java.util.List;

public interface ReviewService {

    List<Review> getLatest(int limit);

}

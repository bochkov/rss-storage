package com.sergeybochkov.rss.itstudent.service;

import com.sergeybochkov.rss.itstudent.domain.Post;

import java.util.List;

public interface PostService {

    List<Post> getLatest();
}

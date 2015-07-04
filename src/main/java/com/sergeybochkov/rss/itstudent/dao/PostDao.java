package com.sergeybochkov.rss.itstudent.dao;

import com.sergeybochkov.rss.itstudent.domain.Post;

import java.util.List;

public interface PostDao {

    List<Post> getLatest();

    Post findByUrl(String url);

    void add(Post post);
}

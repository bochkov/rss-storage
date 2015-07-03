package com.sergeybochkov.rss.itstudent.dao;

import com.sergeybochkov.rss.itstudent.domain.Post;

import java.util.List;

public interface PostDao {

    public List<Post> getLatest();

    public Post findByUrl(String url);

    public void add(Post post);
}

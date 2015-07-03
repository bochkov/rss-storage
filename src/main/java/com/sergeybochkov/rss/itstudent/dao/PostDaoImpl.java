package com.sergeybochkov.rss.itstudent.dao;

import com.sergeybochkov.rss.itstudent.domain.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostDaoImpl implements PostDao {

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public List<Post> getLatest() {
        Query query = new Query().with(new Sort(Sort.Direction.DESC, "timestamp")).limit(30);
        return mongoOperations.find(query, Post.class);
    }

    @Override
    public Post findByUrl(String url) {
        return mongoOperations.findOne(Query.query(Criteria.where("url").is(url)), Post.class);
    }

    @Override
    public void add(Post post) {
        mongoOperations.save(post);
    }
}

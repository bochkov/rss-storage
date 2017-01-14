package com.sergeybochkov.rss.rollingstone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewDaoImpl implements ReviewDao {

    private final MongoOperations ops;

    @Autowired
    public ReviewDaoImpl(MongoOperations ops) {
        this.ops = ops;
    }

    @Override
    public List<Review> getLatest(int limit) {
        return ops.find(new Query()
                .with(new Sort(Sort.Direction.DESC, "date"))
                .limit(limit), Review.class);
    }

    @Override
    public Review find(String id) {
        return ops.findOne(
                Query.query(
                        Criteria.where(id).is(id)),
                Review.class);
    }

    @Override
    public void add(Review review) {
        ops.save(review);
    }
}

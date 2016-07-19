package com.sergeybochkov.rss.rollingstone.dao;

import com.sergeybochkov.rss.rollingstone.domain.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewDaoImpl implements ReviewDao {

    private final MongoOperations mongoOperations;

    @Autowired
    public ReviewDaoImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public List<Review> getLatest() {
        Query query = new Query()
                .with(new Sort(Sort.Direction.DESC, "date"))
                .limit(10);
        return mongoOperations.find(query, Review.class);
    }

    @Override
    public Review find(String id) {
        return mongoOperations.findOne(Query.query(Criteria.where(id).is(id)), Review.class);
    }

    @Override
    public void add(Review review) {
        mongoOperations.save(review);
    }
}

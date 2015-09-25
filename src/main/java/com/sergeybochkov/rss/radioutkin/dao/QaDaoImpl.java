package com.sergeybochkov.rss.radioutkin.dao;

import com.sergeybochkov.rss.radioutkin.domain.Qa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class QaDaoImpl implements QaDao {

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void save(Qa qa) {
        mongoOperations.save(qa);
    }

    @Override
    public Qa get(String id) {
        return mongoOperations.findOne(Query.query(Criteria.where("id").is(id)), Qa.class);
    }

    @Override
    public List<Qa> getAll() {
        return mongoOperations.findAll(Qa.class);
    }

    @Override
    public void remove(String id) {
        mongoOperations.remove(Query.query(Criteria.where("id").is(id)), Qa.class);
    }

    @Override
    public List<Qa> getLatest() {
        Query query = new Query(Criteria.where("timestamp").exists(true)).with(new Sort(Sort.Direction.DESC, "timestamp")).limit(30);
        return mongoOperations.find(query, Qa.class);
    }

    @Override
    public boolean find(Qa qa) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("q_author").is(qa.getQ_author()),
                Criteria.where("published").is(qa.getPublished()),
                Criteria.where("updated").is(qa.getUpdated()),
                Criteria.where("a_text").is(qa.getA_text())
        );

        Query query = new Query(criteria);
        return !mongoOperations.find(query, Qa.class).isEmpty();
    }

    @Override
    public void removeOldest(Date than) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("updated").lte(than),
                Criteria.where("timestamp").lte(than)
        );
        Query query = new Query(criteria);
        mongoOperations.remove(query, Qa.class);
    }
}

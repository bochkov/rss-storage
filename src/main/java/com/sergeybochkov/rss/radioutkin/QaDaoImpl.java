package com.sergeybochkov.rss.radioutkin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QaDaoImpl implements QaDao {

    private final MongoOperations ops;

    @Autowired
    public QaDaoImpl(MongoOperations ops) {
        this.ops = ops;
    }

    @Override
    public void save(Qa qa) {
        ops.save(qa);
    }

    @Override
    public Qa get(String id) {
        return ops.findOne(
                Query.query(Criteria.where("id").is(id)),
                Qa.class);
    }

    @Override
    public List<Qa> getLatest(int limit) {
        return ops.find(
                new Query(Criteria.where("timestamp").exists(true))
                        .with(new Sort(Sort.Direction.DESC, "timestamp"))
                        .limit(limit),
                Qa.class);
    }

    @Override
    public boolean find(Qa qa) {
        return !ops.find(
                new Query(new Criteria().andOperator(
                        Criteria.where("q_author").is(qa.getqAuthor()),
                        Criteria.where("published").is(qa.getPublished()),
                        Criteria.where("updated").is(qa.getUpdated()),
                        Criteria.where("a_text").is(qa.getaText()))),
                Qa.class)
                .isEmpty();
    }

    /*
    public void removeOldest(Date than) {
        ops.remove(
                new Query(new Criteria().orOperator(
                        Criteria.where("updated").lte(than),
                        Criteria.where("timestamp").lte(than))),
                Qa.class);
    }
    */
}

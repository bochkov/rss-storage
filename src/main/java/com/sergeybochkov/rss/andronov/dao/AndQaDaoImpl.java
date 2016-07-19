package com.sergeybochkov.rss.andronov.dao;

import com.sergeybochkov.rss.andronov.domain.AndQa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AndQaDaoImpl implements AndQaDao {

    private final MongoOperations mongo;

    @Autowired
    public AndQaDaoImpl(MongoOperations mongo) {
        this.mongo = mongo;
    }

    @Override
    public void save(AndQa andQa) {
        mongo.save(andQa);
    }

    @Override
    public AndQa get(String id) {
        return mongo.findOne(Query.query(Criteria.where("id").is(id)), AndQa.class);
    }

    @Override
    public List<AndQa> getAll() {
        return mongo.findAll(AndQa.class);
    }

    @Override
    public void remove(String id) {
        mongo.remove(get(id));
    }

    @Override
    public List<AndQa> getLatest() {
        Query query = new Query().with(new Sort(Sort.Direction.DESC, "published")).limit(30);
        return mongo.find(query, AndQa.class);
    }

    @Override
    public boolean find(AndQa andQa) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("qAuthor").is(andQa.getqAuthor()),
                Criteria.where("aText").is(andQa.getaText()),
                Criteria.where("published").is(andQa.getPublished())
        );

        Query query = new Query(criteria);
        return !mongo.find(query, AndQa.class).isEmpty();
    }

    @Override
    public void removeOldest(Date than) {
        Criteria criteria = Criteria.where("timestamp").lte(than);
        Query query = new Query(criteria);
        mongo.remove(query, AndQa.class);
    }
}

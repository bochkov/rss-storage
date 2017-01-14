package com.sergeybochkov.rss.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class StoreDaoImpl implements StoreDao {

    private final MongoOperations ops;

    @Autowired
    public StoreDaoImpl(MongoOperations ops) {
        this.ops = ops;
    }

    @Override
    public void save(Store store) {
        ops.save(store);
    }

    @Override
    public Store get(String key) {
        return ops.findOne(Query.query(Criteria.where("key").is(key)), Store.class);
    }
}

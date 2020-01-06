package com.sergeybochkov.rss.lostfilm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NewsDaoImpl implements NewsDao {

    private final MongoOperations ops;

    @Autowired
    public NewsDaoImpl(MongoOperations ops) {
        this.ops = ops;
    }

    @Override
    public void save(News news) {
        ops.save(news);
    }

    @Override
    public boolean notExists(News news) {
        Query query = Query.query(Criteria.where("articleId").is(news.getArticleId()));
        return ops.find(query, News.class).isEmpty();
    }

    @Override
    public List<News> getLatest() {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "date")).limit(20);
        return ops.find(query, News.class);
    }
}

package com.sergeybochkov.rss.lostfilm.dao;

import com.sergeybochkov.rss.lostfilm.domain.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NewsDaoImpl implements NewsDao {

    private final MongoOperations mongoOperations;

    @Autowired
    public NewsDaoImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void save(News news) {
        mongoOperations.save(news);
    }

    @Override
    public boolean exists(News news) {
        return !mongoOperations.find(Query.query(Criteria.where("articleId").is(news.getArticleId())), News.class).isEmpty();
    }

    @Override
    public List<News> getLatest() {
        Query query = new Query().with(new Sort(Sort.Direction.DESC, "date")).limit(10);
        return mongoOperations.find(query, News.class);
    }
}

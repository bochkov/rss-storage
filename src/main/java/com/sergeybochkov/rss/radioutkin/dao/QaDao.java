package com.sergeybochkov.rss.radioutkin.dao;

import com.sergeybochkov.rss.radioutkin.domain.Qa;

import java.util.Date;
import java.util.List;

public interface QaDao {

    void save(Qa qa);

    Qa get(String id);

    List<Qa> getAll();

    void remove(String id);

    List<Qa> getLatest();

    boolean find(Qa qa);

    void removeOldest(Date than);
}

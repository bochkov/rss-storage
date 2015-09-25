package com.sergeybochkov.rss.andronov.dao;

import com.sergeybochkov.rss.andronov.domain.AndQa;

import java.util.Date;
import java.util.List;

public interface AndQaDao {

    void save(AndQa andQa);

    AndQa get(String id);

    List<AndQa> getAll();

    void remove(String id);

    List<AndQa> getLatest();

    boolean find(AndQa andQa);

    void removeOldest(Date than);
}

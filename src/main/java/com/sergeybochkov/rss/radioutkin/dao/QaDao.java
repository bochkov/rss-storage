package com.sergeybochkov.rss.radioutkin.dao;

import com.sergeybochkov.rss.radioutkin.domain.Qa;

import java.util.Date;
import java.util.List;

public interface QaDao {

    public void save(Qa qa);

    public Qa get(String id);

    public List<Qa> getAll();

    public void remove(String id);

    public List<Qa> getLatest();

    public boolean find(Qa qa);

    public void removeOldest(Date than);
}

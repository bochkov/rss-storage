package com.sergeybochkov.rss.radioutkin.service;

import com.sergeybochkov.rss.radioutkin.domain.Qa;

import java.util.List;

public interface QaService {

    public void add(Qa qa);

    public Qa get(String id);

    public List<Qa> getLatest();

    public boolean find(Qa qa);
}

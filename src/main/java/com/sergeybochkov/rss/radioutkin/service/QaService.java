package com.sergeybochkov.rss.radioutkin.service;

import com.sergeybochkov.rss.radioutkin.domain.Qa;

import java.util.List;

public interface QaService {

    void add(Qa qa);

    Qa get(String id);

    List<Qa> getLatest();

    boolean find(Qa qa);
}

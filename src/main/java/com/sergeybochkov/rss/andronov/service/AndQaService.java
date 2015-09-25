package com.sergeybochkov.rss.andronov.service;

import com.sergeybochkov.rss.andronov.domain.AndQa;

import java.util.List;

public interface AndQaService {

    void add(AndQa andQa);

    AndQa get(String id);

    List<AndQa> getLatest();

    boolean find(AndQa andQa);
}

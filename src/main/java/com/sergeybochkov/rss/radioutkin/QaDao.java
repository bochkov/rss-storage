package com.sergeybochkov.rss.radioutkin;

import java.util.List;

public interface QaDao {

    void save(Qa qa);

    Qa get(String id);

    List<Qa> getLatest(int limit);

    boolean find(Qa qa);
}

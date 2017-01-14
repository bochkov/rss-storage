package com.sergeybochkov.rss.store;

public interface StoreDao {

    void save(Store store);

    Store get(String key);
}

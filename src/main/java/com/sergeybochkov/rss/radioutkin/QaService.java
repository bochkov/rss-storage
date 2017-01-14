package com.sergeybochkov.rss.radioutkin;

import java.util.List;

public interface QaService {

    List<Qa> getLatest(int limit);
}

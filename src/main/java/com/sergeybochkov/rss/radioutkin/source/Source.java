package com.sergeybochkov.rss.radioutkin.source;

import java.io.IOException;

public interface Source {

    void download() throws IOException;
}

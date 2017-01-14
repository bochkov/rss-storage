package com.sergeybochkov.rss.lostfilm.parsing;

import java.text.ParseException;

public interface SourceElement<T> {

    T parse() throws ParseException;

}

package com.sergeybochkov.rss.radioutkin.source;

import com.sergeybochkov.rss.radioutkin.Qa;
import com.sergeybochkov.rss.radioutkin.QaDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

public final class RadioUtkin extends AbstractSource {

    private static final Logger LOG = LoggerFactory.getLogger(RadioUtkin.class);

    private static final String RADIOUTKIN_URL = "http://radioutkin.ru/conference/";

    private final QaDao qaDao;

    public RadioUtkin(QaDao qaDao) {
        this.qaDao = qaDao;
    }

    @Override
    public void download() throws IOException {
        int created = 0, dropped = 0;
        Document doc = Jsoup
                .connect(RADIOUTKIN_URL)
                .get();
        for (Element elem : doc.getElementsByClass("guest-book__item")) {
            String link = elem.getElementsByClass("guest-book__post-link").get(0).attr("href");
            String id = link.substring(link.lastIndexOf("=") + 1);
            if (qaDao.get(id) == null) {
                Qa qa = new Qa(id, String.format("%s%s", RADIOUTKIN_URL, link), new Date(),
                        normalize(elem.getElementsByClass("guest-book__message-text").text()),
                        parseAuthor(elem.getElementsByClass("guest-book__author").text()),
                        parseDate(elem.getElementsByClass("guest-book__author").text()),
                        normalize(elem.getElementsByClass("guest-book-answer__message").text()),
                        parseAuthor(elem.getElementsByClass("guest-book-answer__author").text()),
                        parseDate(elem.getElementsByClass("guest-book-answer__author").text()));
                qaDao.save(qa);
                ++created;
            } else
                ++dropped;
        }
        LOG.info(String.format("RADIOUTKIN: %s created, %s dropped", created, dropped));
    }

    @Override
    public Logger log() {
        return LOG;
    }
}

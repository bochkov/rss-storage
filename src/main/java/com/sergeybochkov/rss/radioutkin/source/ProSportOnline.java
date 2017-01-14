package com.sergeybochkov.rss.radioutkin.source;

import com.sergeybochkov.rss.radioutkin.Qa;
import com.sergeybochkov.rss.radioutkin.QaDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

public final class ProSportOnline extends AbstractSource {

    private static final Logger LOG = LoggerFactory.getLogger(ProSportOnline.class);
    private static final String PROSPORT_URL = "http://prosport-online.ru/Utkin/conference";

    private final QaDao qaDao;

    public ProSportOnline(QaDao qaDao) {
        this.qaDao = qaDao;
    }

    @Override
    public void download() throws IOException {
        int created = 0, dropped = 0;
        Document doc = Jsoup.connect(PROSPORT_URL)
                .get();
        Elements elements = doc.getElementsByClass("q_block");
        for (int i = elements.size() - 1; i >= 0; --i) {
            Element elem = elements.get(i);
            Element question = elem.getElementsByClass("question").get(0);
            Element answer = elem.getElementsByClass("answer").get(0);
            Qa qa = new Qa(null, "", new Date(),
                    normalize(question.children().get(1).text()),
                    parseAuthor(question.children().get(0).text()),
                    parseDate(question.children().get(0).text()),
                    answer.children().get(1).text(),
                    parseAuthor(answer.children().get(0).text()),
                    parseDate(answer.children().get(0).text()));
            if (!qaDao.find(qa)) {
                qaDao.save(qa);
                ++created;
            }
            else
                ++dropped;
        }
        LOG.info(String.format("PROSPORT: %s created, %s dropped", created, dropped));
    }

    @Override
    public Logger log() {
        return LOG;
    }
}

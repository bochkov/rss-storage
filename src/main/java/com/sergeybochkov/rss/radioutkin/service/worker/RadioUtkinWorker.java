package com.sergeybochkov.rss.radioutkin.service.worker;

import com.sergeybochkov.rss.radioutkin.domain.Qa;
import com.sergeybochkov.rss.radioutkin.service.QaService;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Date;

import static com.sergeybochkov.rss.radioutkin.service.worker.WorkerCommon.*;
import static java.nio.file.Paths.get;

public class RadioUtkinWorker implements Runnable {

    private static final Logger LOG = Logger.getLogger(RadioUtkinWorker.class);

    private static final String RADIOUTKIN_URL = "http://radioutkin.ru/conference/";

    private QaService service;

    public RadioUtkinWorker(QaService service) {
        this.service = service;
    }

    @Override
    public void run() {
        try {
            downloadFromRadioutkin();
        } catch (IOException ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }

    private void downloadFromRadioutkin() throws IOException {
        int created = 0;
        int dropped = 0;

        Document doc = Jsoup.connect(RADIOUTKIN_URL).get();
        for (Element elem : doc.getElementsByClass("guest-book__item")) {
            String link = elem.getElementsByClass("guest-book__post-link").get(0).attr("href");
            String id = link.substring(link.lastIndexOf("=") + 1);
            if (get(id) == null) {
                Qa qa = new Qa();
                qa.setId(id);
                qa.setLink(RADIOUTKIN_URL + link);
                qa.setTimestamp(new Date());

                qa.setUpdated(parseDate(elem.getElementsByClass("guest-book-answer__author").text()));
                qa.setPublished(parseDate(elem.getElementsByClass("guest-book__author").text()));

                qa.setQ_author(parseAuthor(elem.getElementsByClass("guest-book__author").text()));
                qa.setQ_text(normalize(elem.getElementsByClass("guest-book__message-text").text()));
                qa.setA_author(parseAuthor(elem.getElementsByClass("guest-book-answer__author").text()));
                qa.setA_text(normalize(elem.getElementsByClass("guest-book-answer__message").text()));

                service.add(qa);
                ++created;
            } else
                ++dropped;
        }

        LOG.info(String.format("RADIOUTKIN: %s created, %s dropped", created, dropped));
    }


}

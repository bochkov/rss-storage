package com.sergeybochkov.rss.radioutkin.service.worker;

import com.sergeybochkov.rss.radioutkin.domain.Qa;
import com.sergeybochkov.rss.radioutkin.service.QaService;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Date;

import static com.sergeybochkov.rss.radioutkin.service.worker.WorkerCommon.*;

public class ProSportOnlineWorker implements Runnable {

    private static final Logger LOG = Logger.getLogger(ProSportOnlineWorker.class);

    private static final String PROSPORT_URL = "http://prosport-online.ru/Utkin/conference";

    private QaService service;

    public ProSportOnlineWorker(QaService service) {
        this.service = service;
    }

    @Override
    public void run() {
        try {
            downloadFromProsport();
        }
        catch (IOException ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }

    public void downloadFromProsport() throws IOException {
        int created = 0;
        int dropped = 0;

        Document doc = Jsoup.connect(PROSPORT_URL).get();
        Elements elements = doc.getElementsByClass("q_block");
        for (int i = elements.size() - 1; i >= 0; --i) {
            Element elem = elements.get(i);
            Qa qa = new Qa();
            qa.setTimestamp(new Date());

            Element question = elem.getElementsByClass("question").get(0);
            Element answer = elem.getElementsByClass("answer").get(0);

            qa.setQ_author(parseAuthor(question.children().get(0).text()));
            qa.setQ_text(normalize(question.children().get(1).text()));
            qa.setPublished(parseDate(question.children().get(0).text()));

            qa.setA_author(parseAuthor(answer.children().get(0).text()));
            qa.setA_text(answer.children().get(1).text());
            qa.setUpdated(parseDate(answer.children().get(0).text()));

            if (!service.find(qa)) {
                service.add(qa);
                ++created;
            }
            else
                ++dropped;
        }

        LOG.info(String.format("PROSPORT: %s created, %s dropped", created, dropped));
    }
}

package com.sergeybochkov.rss.radioutkin.service;

import com.sergeybochkov.rss.radioutkin.dao.QaDao;
import com.sergeybochkov.rss.radioutkin.domain.Qa;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QaServiceImpl implements QaService {

    @Autowired
    private QaDao qaDao;

    @Override
    public void add(Qa qa) {
        qaDao.save(qa);
    }

    @Override
    public Qa get(String id) {
        return qaDao.get(id);
    }

    @Override
    public List<Qa> getLatest() {
        return qaDao.getLatest();
    }

    @Override
    public boolean find(Qa qa) {
        return qaDao.find(qa);
    }

    private static final String RADIOUTKIN_URL = "http://radioutkin.ru/conference/";
    private static final String PROSPORT_URL = "http://prosport-online.ru/Utkin/conference";

    private static final Pattern pattern = Pattern.compile("^(.+) (пишет|отвечает) (\\d+).(\\d+).(\\d+) в (\\d+):(\\d+)$");

    private static final Logger logger = Logger.getLogger(QaServiceImpl.class.getName());

    private String parseAuthor(String value) {
        Matcher matcher = pattern.matcher(value);
        if (matcher.find())
            return matcher.group(1);
        return "";
    }

    private Date parseDate(String value){
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(matcher.group(3)));
            cal.set(Calendar.MONTH, Integer.parseInt(matcher.group(4)) - 1);
            cal.set(Calendar.YEAR, Integer.parseInt(matcher.group(5)));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher.group(6)));
            cal.set(Calendar.MINUTE, Integer.parseInt(matcher.group(7)));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }

        return null;
    }

    private static String normalize(String value) {
        String rezult = value
                .trim()
                .replaceAll("\\\\", "")
                .replace("\r\n", "\r");

        Pattern linkPattern = Pattern.compile("(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w\\?=.-]*)*/?");
        Matcher matcher = linkPattern.matcher(rezult);
        if (matcher.find())
            rezult = matcher.replaceAll("<a href=\"" + matcher.group() + "\">" + matcher.group() + "</a>");

        return rezult;
    }

    @Transactional
    @Scheduled(cron="0 */15 * * * ?")
    public void download() {
        new Thread(() -> {
            try {
                downloadFromRadioutkin();
            }
            catch (IOException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }, "radioutkin-spawner").start();
        new Thread(() -> {
            try {
                downloadFromProsport();
            }
            catch (IOException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }, "prosport-online-spawner").start();
    }

    public void downloadFromRadioutkin() throws IOException {
        int created = 0;
        int dropped = 0;

        Document doc = Jsoup.connect(RADIOUTKIN_URL).get();
        for (Element elem : doc.getElementsByClass("guest-book__item")){
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

                add(qa);
                ++created;
            }
            else
                ++dropped;
        }

        logger.info(String.format("RADIOUTKIN: %s created, %s dropped", created, dropped));
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

            if (!find(qa)) {
                ++created;
                add(qa);
            }
            else
                ++dropped;
        }

        logger.info(String.format("PROSPORT: %s created, %s dropped", created, dropped));
    }

    public void clean() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -2);
        qaDao.removeOldest(cal.getTime());
    }
}

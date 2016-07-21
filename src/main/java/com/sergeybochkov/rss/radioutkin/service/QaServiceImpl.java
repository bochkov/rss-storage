package com.sergeybochkov.rss.radioutkin.service;

import com.sergeybochkov.rss.radioutkin.dao.QaDao;
import com.sergeybochkov.rss.radioutkin.domain.Qa;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class QaServiceImpl implements QaService {

    private static final String RADIOUTKIN_URL = "http://radioutkin.ru/conference/";
    private static final String PROSPORT_URL = "http://prosport-online.ru/Utkin/conference";

    private static final String SOVSPORT_URL = "http://www.sovsport.ru/conf-item/15";
    private static final int SOVSPORT_OBJECT = 15;

    private static final Pattern pattern = Pattern.compile("^(.+) (пишет|отвечает) (\\d+).(\\d+).(\\d+) в (\\d+):(\\d+)$");

    private static final Logger LOG = Logger.getLogger(QaServiceImpl.class.getName());

    private ExecutorService executor = Executors.newFixedThreadPool(3);

    private final QaDao qaDao;

    @Autowired
    public QaServiceImpl(QaDao qaDao) {
        this.qaDao = qaDao;
    }

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

        Pattern linkPattern = Pattern.compile("(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w?=.-]*)*/?");
        Matcher matcher = linkPattern.matcher(rezult);
        if (matcher.find())
            rezult = matcher.replaceAll("<a href=\"" + matcher.group() + "\">" + matcher.group() + "</a>");

        return rezult;
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void download() {
        //executor.submit(this::downloadFromRadioutkin);
        //executor.submit(this::downloadFromProsport);
        executor.submit(this::downloadFromSovSport);
    }

    public QaReturn downloadFromSovSport() throws IOException {
        QaReturn ret = new QaReturn();



        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        int pageNum = 1;
        while (true) {
            String commentsUrl = String.format("http://www.sovsport.ru/js/comment85_%s_%s.js?t=%s",
                    SOVSPORT_OBJECT, pageNum, System.currentTimeMillis());
            String comments = getContent(commentsUrl).split("];")[0] + "];";

            try {
                engine.eval(comments);
                ScriptObjectMirror map = (ScriptObjectMirror) engine.get("arr");
                if (map.isEmpty())
                    break;
                //printRec(map, 0);
                for (String key : map.keySet()) {
                    Qa qa = new Qa();
                    qa.setTimestamp(new Date());
                    handle(map.get(key), qa);
                    System.out.println(qa);
                }
            } catch (ScriptException ex) {
                ex.printStackTrace();
            }

            ++pageNum;
        }

        LOG.info(String.format("SOVSPORT: %s created, %s dropped", ret.getCreated(), ret.getDropped()));
        return ret;
    }

    private void handle(Object map, Qa qa) {
        if (map instanceof ScriptObjectMirror) {
            ScriptObjectMirror som = (ScriptObjectMirror) map;
            qa.setId(String.valueOf(som.get("0")));
            qa.setQ_author(String.valueOf(som.get("4")));
            qa.setQ_text(String.valueOf(som.get("6")));

            ScriptObjectMirror sm1 = (ScriptObjectMirror) som.get("9");
            if (sm1.containsKey("0"))
                handleAnswer(sm1.get("0"), qa);
        }
    }

    private void handleAnswer(Object map, Qa qa) {
        if (map instanceof ScriptObjectMirror) {
            ScriptObjectMirror som = (ScriptObjectMirror) map;
            qa.setA_author(String.valueOf(som.get("4")));
            qa.setA_text(String.valueOf(som.get("6")));
        }
    }

    private void printRec(ScriptObjectMirror object, int add) {
        for (String key : object.keySet()) {
            Object obj = object.get(key);
            for (int i = 0; i < add; ++i)
                System.out.print("\t");
            System.out.println(key + ":" + obj);
            if (obj instanceof ScriptObjectMirror)
                printRec((ScriptObjectMirror) obj, add + 1);
        }
    }

    private String getContent(String url) {
        String res = "";
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null)
                    res += line;
            }
        }
        catch (IOException ex) {
            //
        }
        return res;
    }

    public QaReturn downloadFromRadioutkin() throws IOException {
        QaReturn ret = new QaReturn();

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
                ret.incCreated();
            }
            else
                ret.incDropped();
        }

        LOG.info(String.format("RADIOUTKIN: %s created, %s dropped", ret.getCreated(), ret.getDropped()));
        return ret;
    }

    public QaReturn downloadFromProsport() throws IOException {
        QaReturn ret = new QaReturn();

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
                add(qa);
                ret.incCreated();
            }
            else
                ret.incDropped();
        }

        LOG.info(String.format("PROSPORT: %s created, %s dropped", ret.getCreated(), ret.getDropped()));
        return new QaReturn();
    }

    //@Scheduled(cron = "0 */30 * * * ?")
    public void clean() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -2);
        qaDao.removeOldest(cal.getTime());
    }

    private class QaReturn {
        public int dropped;
        public int created;

        public QaReturn() {
            this(0, 0);
        }

        public QaReturn(int dropped, int created) {
            this.dropped = dropped;
            this.created = created;
        }

        public void incDropped() {
            ++dropped;
        }

        public void incCreated() {
            ++created;
        }

        public int getDropped() {
            return dropped;
        }

        public void setDropped(int dropped) {
            this.dropped = dropped;
        }

        public int getCreated() {
            return created;
        }

        public void setCreated(int created) {
            this.created = created;
        }
    }
}

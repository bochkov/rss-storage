package com.sergeybochkov.rss.radioutkin.source;

import com.sergeybochkov.rss.radioutkin.Qa;
import com.sergeybochkov.rss.radioutkin.QaDao;
import com.sergeybochkov.rss.store.Store;
import com.sergeybochkov.rss.store.StoreDao;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SovSport extends AbstractSource {

    private static final Logger LOG = LoggerFactory.getLogger(SovSport.class);

    //private static final String SOVSPORT_URL = "http://www.sovsport.ru/conf-item/15";
    private static final String SOVSPORT_URL = "http://www.sovsport.ru/js/comment85_%s_%s.js?t=%s";
    private static final int SOVSPORT_OBJECT = 15;
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d+)\u00a0(.*),\\s+(\\d+):(\\d+)");

    private final QaDao qaDao;
    private final StoreDao storeDao;
    private final ScriptEngine jsEngine;

    public SovSport(QaDao qaDao, StoreDao storeDao) {
        this.qaDao = qaDao;
        this.storeDao = storeDao;
        this.jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    }

    @Override
    public void download() throws IOException {
        int created = 0, dropped = 0, updated = 0;
        Store store = storeDao.get("page");
        int pageNum = store == null ? 1 : Integer.parseInt(store.getValue());
        while (true) {
            LOG.info("Processing page=" + pageNum);
            String commentsUrl = String.format(SOVSPORT_URL, SOVSPORT_OBJECT, pageNum, System.currentTimeMillis());
            String comments = getContent(commentsUrl).split("];")[0] + "];";
            try {
                jsEngine.eval(comments);
                ScriptObjectMirror map = (ScriptObjectMirror) jsEngine.get("arr");
                if (!map.isEmpty()) {
                    //printRec(map, 0);
                    for (String key : map.keySet()) {
                        Qa qa = new Qa();
                        handle(map.get(key), qa);
                        Qa inDbQa = qaDao.get(qa.getId());
                        if (inDbQa == null) {
                            if (qa.isPublished()) {
                                qa.setTimestamp(new Date());
                                qaDao.save(qa);
                                ++created;
                            } else
                                ++dropped;
                        } else {
                            if (!inDbQa.equals(qa)) {
                                ++updated;
                                qa.setTimestamp(new Date());
                                qaDao.save(qa);
                            } else
                                ++dropped;
                        }
                    }
                    storeDao.save(new Store("page", String.format("%s", pageNum)));
                }
                else
                    throw new ScriptException("No more arr");
            } catch (ScriptException ex) {
                LOG.warn(ex.getMessage(), ex);
                break;
            }
            ++pageNum;
        }
        LOG.info(String.format("SOVSPORT: %s created, %s dropped, %s updated", created, dropped, updated));
    }

    private void handle(Object map, Qa qa) {
        if (map instanceof ScriptObjectMirror) {
            ScriptObjectMirror som = (ScriptObjectMirror) map;
            qa.setId(String.valueOf(som.get("0")));
            qa.setqAuthor(String.valueOf(som.get("4")));
            qa.setqText(String.valueOf(som.get("6")));
            qa.setPublished(getDate(String.valueOf(som.get("5"))));
            ScriptObjectMirror sm1 = (ScriptObjectMirror) som.get("9");
            if (sm1.containsKey("0"))
                handleAnswer(sm1.get("0"), qa);
        }
    }

    private void handleAnswer(Object map, Qa qa) {
        if (map instanceof ScriptObjectMirror) {
            ScriptObjectMirror som = (ScriptObjectMirror) map;
            qa.setaAuthor(String.valueOf(som.get("4")));
            qa.setaText(String.valueOf(som.get("6")));
            qa.setUpdated(getDate(String.valueOf(som.get("5"))));
        }
    }

    @SuppressWarnings("unused")
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
        StringBuilder res = new StringBuilder();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null)
                    res.append(line);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return res.toString();
    }

    private Date getDate(String value) {
        Matcher m = DATE_PATTERN.matcher(value);
        if (m.find()) {
            Calendar cal = new GregorianCalendar();
            cal.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(1)));
            cal.set(Calendar.MONTH, Qa.MONTHS.indexOf(m.group(2)));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(3)));
            cal.set(Calendar.MINUTE, Integer.parseInt(m.group(4)));
            cal.set(Calendar.SECOND, 0);
            return cal.getTime();
        }
        return null;
    }

    @Override
    public Logger log() {
        return LOG;
    }
}

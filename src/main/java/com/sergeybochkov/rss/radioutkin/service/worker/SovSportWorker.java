package com.sergeybochkov.rss.radioutkin.service.worker;

import com.sergeybochkov.rss.radioutkin.domain.Qa;
import com.sergeybochkov.rss.radioutkin.service.QaService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.log4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SovSportWorker implements Runnable {

    private static final Logger LOG = Logger.getLogger(SovSportWorker.class);

    //private static final String SOVSPORT_URL = "http://www.sovsport.ru/conf-item/15";
    private static final int SOVSPORT_OBJECT = 15;

    private static final List<String> months = Arrays.asList("января", "февраля", "марта",
            "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря");

    private QaService service;

    public SovSportWorker(QaService service) {
        this.service = service;
    }

    @Override
    public void run() {
        try {
            downloadFromSovSport();
        }
        catch (IOException ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }

    private void downloadFromSovSport() throws IOException {
        int created = 0;
        int dropped = 0;
        int updated = 0;

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        int pageNum = 1;
        while (true) {
            LOG.info("Processing page=" + pageNum);
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
                    handle(map.get(key), qa);

                    Qa inDbQa = service.get(qa.getId());
                    if (inDbQa == null) {
                        if (qa.isPublished()) {
                            qa.setTimestamp(new Date());
                            service.add(qa);
                            ++created;
                        }
                        else
                            ++dropped;
                    }
                    else {
                        if (!inDbQa.equals(qa)) {
                            ++updated;
                            qa.setTimestamp(new Date());
                            service.add(qa);
                        }
                        else
                            ++dropped;
                    }
                }
            } catch (ScriptException ex) {
                LOG.warn(ex);
            }

            ++pageNum;
        }
        LOG.info(String.format("SOVSPORT: %s created, %s dropped, %s updated", created, dropped, updated));
    }

    private void handle(Object map, Qa qa) {
        if (map instanceof ScriptObjectMirror) {
            ScriptObjectMirror som = (ScriptObjectMirror) map;
            qa.setId(String.valueOf(som.get("0")));
            qa.setQ_author(String.valueOf(som.get("4")));
            qa.setQ_text(String.valueOf(som.get("6")));
            qa.setPublished(getDate(String.valueOf(som.get("5"))));

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

    private static final Pattern datePattern = Pattern.compile("(\\d+)\u00a0(.*),\\s+(\\d+):(\\d+)");

    private Date getDate(String value) {
        Matcher m = datePattern.matcher(value);
        if (m.find()) {
            Calendar cal = new GregorianCalendar();
            cal.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(1)));
            cal.set(Calendar.MONTH, months.indexOf(m.group(2)));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(3)));
            cal.set(Calendar.MINUTE, Integer.parseInt(m.group(4)));
            cal.set(Calendar.SECOND, 0);
            return cal.getTime();
        }
        return null;
    }
}

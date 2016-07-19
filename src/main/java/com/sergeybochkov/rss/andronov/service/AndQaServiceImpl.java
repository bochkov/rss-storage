package com.sergeybochkov.rss.andronov.service;

import com.sergeybochkov.rss.andronov.dao.AndQaDao;
import com.sergeybochkov.rss.andronov.domain.AndQa;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AndQaServiceImpl implements AndQaService {

    private static final Logger LOG = Logger.getLogger(AndQaServiceImpl.class.getName());
    private static final String URL = "http://rsport.ru/conference/20130125/641604312.html?conference_id=657785746";

    private final AndQaDao dao;

    @Autowired
    public AndQaServiceImpl(AndQaDao dao) {
        this.dao = dao;
    }

    @Override
    public void add(AndQa andQa) {
        dao.save(andQa);
    }

    @Override
    public AndQa get(String id) {
        return dao.get(id);
    }

    @Override
    public List<AndQa> getLatest() {
        return dao.getLatest();
    }

    @Override
    public boolean find(AndQa andQa) {
        return dao.find(andQa);
    }

    private Pattern pattern = Pattern.compile("(\\d+):(\\d+) (\\d+)\\.(\\d+)\\.(\\d+)");

    private Date parseDate(String str) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(matcher.group(3)));
            cal.set(Calendar.MONTH, Integer.parseInt(matcher.group(4)) - 1);
            cal.set(Calendar.YEAR, Integer.parseInt(matcher.group(5)));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher.group(1)));
            cal.set(Calendar.MINUTE, Integer.parseInt(matcher.group(2)));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }

        return null;
    }

    @Transactional
    @Scheduled(cron = "0 */30 * * * ?")
    public void donwload() throws IOException {
        int created = 0;
        int dropped = 0;

        Document doc = Jsoup.connect(URL).get();
        for (Element elem : doc.getElementsByClass("conference-question-item")) {
            AndQa andQa = new AndQa();
            Element question = elem.getElementsByClass("question").get(0);
            Element answer = elem.getElementsByClass("answer").get(0);

            andQa.setqAuthor(question.getElementsByClass("user").get(0).text());
            andQa.setPublished(parseDate(question.getElementsByClass("title").get(0).text()));
            andQa.setqText(question.getElementsByClass("body").get(0).text());
            andQa.setLink("http://rsport.ru" + question.getElementsByClass("body").get(0).attr("href"));

            andQa.setaAuthor(answer.getElementsByClass("user").get(0).text());
            andQa.setaText(answer.getElementsByClass("body").get(0).text());

            if (!find(andQa)) {
                ++created;
                add(andQa);
            }
            else
                ++dropped;
        }

        LOG.info(String.format("Конференция Андронова: %s created, %s dropped", created, dropped));
    }
}

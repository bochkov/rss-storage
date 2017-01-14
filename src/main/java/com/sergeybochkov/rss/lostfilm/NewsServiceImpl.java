package com.sergeybochkov.rss.lostfilm;

import com.sergeybochkov.rss.lostfilm.parsing.ArticleElement;
import com.sergeybochkov.rss.lostfilm.parsing.BodyElement;
import com.sergeybochkov.rss.lostfilm.parsing.DateElement;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public final class NewsServiceImpl implements NewsService {

    private static final Logger LOG = LoggerFactory.getLogger(NewsServiceImpl.class);
    private static final String URL = "http://www.lostfilm.tv";

    private final NewsDao newsDao;

    @Value("http.user-agent")
    private String userAgent;

    @Autowired
    public NewsServiceImpl(NewsDao newsDao) {
        this.newsDao = newsDao;
    }

    @Transactional
    @Scheduled(cron="0 0 * * * ?")
    public void download() throws IOException, ParseException {
        Connection.Response response = Jsoup.connect(URL)
                .userAgent(userAgent)
                .execute();
        if (response.statusCode() == 200) {
            int created = 0, dropped = 0;
            for (Element element : response.parse()
                    .getElementsByClass("content_body").get(0)
                    .getElementsByTag("h1")) {
                News news = extractData(element);
                if (!newsDao.exists(news)) {
                    newsDao.save(news);
                    ++created;
                }
                else
                    ++dropped;
            }
            LOG.info(String.format("LostFilm: %s created, %s dropped", created, dropped));
        } else {
            LOG.warn("Сервис недоступен");
        }
    }

    private News extractData(Element element) throws IOException, ParseException {
        String title = element.text();
        element = element.nextElementSibling();
        String imgUrl = String.format("%s%s", URL, element.children().get(0).attr("src"));
        while (!element.tagName().equals("table"))
            element = element.nextElementSibling();
        Date date = new DateElement(element).parse();
        String fullUrl = String.format("%s%s", URL,
                element.getElementsByClass("a_full_news").get(0).attr("href"));
        String text = new BodyElement(fullUrl, userAgent).parse();
        Integer articleId = new ArticleElement(fullUrl).parse();
        return new News(articleId, date, title, text, fullUrl, imgUrl);
    }

    @Override
    public List<News> getLatest() {
        return newsDao.getLatest();
    }
}

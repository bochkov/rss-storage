package com.sergeybochkov.rss.lostfilm;

import com.sergeybochkov.rss.lostfilm.parsing.ArticleElement;
import com.sergeybochkov.rss.lostfilm.parsing.BodyElement;
import com.sergeybochkov.rss.lostfilm.parsing.DateElement;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger LOG = LoggerFactory.getLogger(NewsServiceImpl.class);
    private static final String URL = "https://www.lostfilm.tv";

    private final NewsDao newsDao;

    @Value("http.user-agent")
    private String userAgent;

    @Autowired
    public NewsServiceImpl(NewsDao newsDao) {
        this.newsDao = newsDao;
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    public void download() throws IOException {
        Connection.Response response = Jsoup
                .connect(String.format("%s/news/", URL))
                .userAgent(userAgent)
                .execute();
        if (response.statusCode() == 200) {
            int created = 0, dropped = 0;
            for (Element element : response.parse()
                    .getElementsByClass("row")) {
                String url = String.format("%s%s", URL, element.attr("href"));
                Connection.Response res = Jsoup
                        .connect(url)
                        .userAgent(userAgent)
                        .execute();
                try {
                    News news = extractData(res.parse(), url);
                    if (!newsDao.exists(news)) {
                        newsDao.save(news);
                        ++created;
                    } else
                        ++dropped;
                } catch (ParseException ex) {
                    LOG.warn(String.format("Пропускаем %s: %s", url, ex.getMessage()));
                    ++dropped;
                }
            }
            LOG.info(String.format("LostFilm: %s created, %s dropped", created, dropped));
        } else {
            LOG.warn("Сервис недоступен");
        }
    }

    @SuppressWarnings("unused")
    @Transactional
    public void downloadAll(String lastUrl) throws IOException {
        String currentUrl = "";
        int id = 1;
        while (!currentUrl.equalsIgnoreCase(lastUrl)) {
            currentUrl = String.format("%s/news/id%s", URL, ++id);
            Connection.Response response = Jsoup
                    .connect(currentUrl)
                    .userAgent(userAgent)
                    .execute();
            if (response.statusCode() == 200) {
                try {
                    News news = extractData(response.parse(), currentUrl);
                    if (!newsDao.exists(news)) {
                        newsDao.save(news);
                    }
                } catch (IOException | ParseException ex) {
                    LOG.warn(String.format("Пропускаем %s: %s", currentUrl, ex.getMessage()));
                }
            }
        }
    }

    private News extractData(Document doc, String url) throws ParseException {
        Elements headers = doc.getElementsByClass("news-header");
        if (headers.isEmpty())
            throw new ParseException("no page", 1);
        Elements bodies = doc.getElementsByClass("news_text_block");
        if (bodies.isEmpty())
            throw new ParseException("no page", 2);
        return new News(
                new ArticleElement(url).parse(),
                new DateElement(headers.get(0).getElementsByClass("date").get(0)).parse(),
                headers.get(0).getElementsByClass("title").get(0).text(),
                new BodyElement(bodies.get(0), URL).parse(),
                url,
                String.format("https:%s", headers.get(0).getElementsByClass("thumb").get(0).attr("src")));
    }

    @Override
    public List<News> getLatest() {
        return newsDao.getLatest();
    }
}

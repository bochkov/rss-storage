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
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.ParseException;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger LOG = LoggerFactory.getLogger(NewsServiceImpl.class);
    private static final String URL = "https://lostfilm.tv";

    private final NewsDao newsDao;

    @Value("${rss.user-agent}")
    private String userAgent;
    @Value("${rss.proxy-host}")
    private String proxyHost;
    @Value("${rss.proxy-port}")
    private Integer proxyPort;

    @Autowired
    public NewsServiceImpl(NewsDao newsDao) {
        this.newsDao = newsDao;
    }

    private Connection connection(String url) {
        Connection con = Jsoup.connect(url).userAgent(userAgent);
        if (proxyHost != null && !proxyHost.isEmpty() && proxyPort != null && proxyPort > 0) {
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
            return con.proxy(proxy);
        }
        return con;
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    public void download() throws IOException {
        Connection.Response response = connection(String.format("%s/news/", URL)).execute();
        if (response.statusCode() == 200) {
            int created = 0;
            int dropped = 0;
            for (Element element : response.parse()
                    .getElementsByClass("row")) {
                String url = String.format("%s%s", URL, element.attr("href"));
                Connection.Response res = Jsoup
                        .connect(url)
                        .userAgent(userAgent)
                        .execute();
                try {
                    News news = extractData(res.parse(), url);
                    if (newsDao.notExists(news)) {
                        newsDao.save(news);
                        ++created;
                    } else
                        ++dropped;
                } catch (ParseException ex) {
                    LOG.warn(String.format("Пропускаем %s: %s", url, ex.getMessage()));
                    ++dropped;
                }
            }
            LOG.info("LostFilm: {} created, {} dropped", created, dropped);
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
            Connection.Response response = connection(currentUrl).execute();
            if (response.statusCode() == 200) {
                try {
                    News news = extractData(response.parse(), currentUrl);
                    if (newsDao.notExists(news)) {
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
        News news = new News();
        news.setArticleId(new ArticleElement(url).parse());
        news.setDate(new DateElement(headers.get(0).getElementsByClass("date").get(0)).parse());
        news.setTitle(headers.get(0).getElementsByClass("title").get(0).text());
        news.setText(new BodyElement(bodies.get(0), URL).parse());
        news.setUrl(url);
        news.setImgUrl("https:" + headers.get(0).getElementsByClass("thumb").get(0).attr("src"));
        return news;
    }

    @Override
    public List<News> getLatest() {
        return newsDao.getLatest();
    }
}

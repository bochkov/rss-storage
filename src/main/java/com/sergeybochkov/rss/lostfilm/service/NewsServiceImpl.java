package com.sergeybochkov.rss.lostfilm.service;

import com.sergeybochkov.rss.lostfilm.dao.NewsDao;
import com.sergeybochkov.rss.lostfilm.domain.News;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsDao newsDao;

    private static final String url = "http://www.lostfilm.tv";
    private static final String userAgent = "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0";

    private static final Logger logger = Logger.getLogger(NewsServiceImpl.class.getName());

    @Transactional
    @Scheduled(cron="0 0 * * * ?")
    public void download() throws IOException, ParseException {

        Connection.Response response = Jsoup.connect(url)
                .userAgent(userAgent)
                .execute();
        if (response.statusCode() != 200)
            logger.warn("Сервис недоступен");

        Document doc = response.parse();
        Element contentBody = doc.getElementsByClass("content_body").get(0);

        int created = 0;
        int dropped = 0;

        for (Element element : contentBody.getElementsByTag("h1")) {
            News news = new News();

            String title = element.text();
            news.setTitle(title);

            element = element.nextElementSibling();
            String imgUrl = url + element.children().get(0).attr("src");
            news.setImgUrl(imgUrl);

            element = element.nextElementSibling();
            while (!element.tagName().equals("table"))
                element = element.nextElementSibling();

            String dateStr = element.getElementsByClass("micro").get(0).text();
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Pattern pattern = Pattern.compile("Дата: (.*)\\..*?Комментариев.*");
            Matcher matcher = pattern.matcher(dateStr);
            Date date = null;
            if (matcher.find())
                date = df.parse(matcher.group(1));
            news.setDate(date);

            String postUrl = element.getElementsByClass("a_full_news").get(0).attr("href");
            news.setUrl(url + postUrl);

            Document fullNews = Jsoup.connect(news.getUrl())
                    .userAgent(userAgent)
                    .get();
            String html;
            Element body = fullNews.getElementsByClass("content_body").get(0);
            body.getElementsByAttributeValueContaining("style", "display:block").remove();
            html = body.html();
            if (html.contains("src=\"/"))
                html = html.replaceAll("src=\"/", "src=\"" + url + "/");
            news.setText(html.trim());

            pattern = Pattern.compile(".*?id=(\\d+).*?");
            matcher = pattern.matcher(postUrl);
            Integer articleId = null;
            if (matcher.find())
                articleId = Integer.parseInt(matcher.group(1));
            news.setArticleId(articleId);

            if (!newsDao.exists(news)) {
                newsDao.save(news);
                ++created;
            }
            else
                ++dropped;
        }

        logger.info(String.format("LostFilm: %s created, %s dropped", created, dropped));
    }

    @Override
    public List<News> getLatest() {
        return newsDao.getLatest();
    }

    public void clean() {
        // todo clean old records
    }
}

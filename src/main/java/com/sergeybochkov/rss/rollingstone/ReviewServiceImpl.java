package com.sergeybochkov.rss.rollingstone;

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
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private static final String URL = "http://rollingstone.ru/review/";
    private static final DateFormatSymbols DF_SYM = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня",
                    "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};
        }
    };

    private final Map<Integer, String> ratingMap = new HashMap<>();
    private final ReviewDao reviewDao;

    @Value("http.user-agent")
    private String userAgent;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
        ratingMap.put(0, "Никак");
        ratingMap.put(1, "Очень слабо");
        ratingMap.put(2, "Неуд");
        ratingMap.put(3, "На троечку");
        ratingMap.put(4, "Хорошо");
        ratingMap.put(5, "Отлично!");
    }

    private String formatRating(Elements stars) {
        int rating = 0;
        for (Element star : stars)
            if (star.child(0).attr("src").contains("star-red.gif"))
                rating += 1;
        return ratingMap.get(rating);
    }

    @Transactional
    @Scheduled(cron = "0 0 */3 * * ?")
    public void download() throws IOException, ParseException {
        Connection.Response response = Jsoup.connect(URL)
                .userAgent(userAgent)
                .followRedirects(true)
                .execute();
        if (response.statusCode() == 200) {
            int created = 0;
            int dropped = 0;
            for (Element article : response.parse().getElementsByClass("tbl-review-inner")) {
                String url = String.format("http://rollingstone.ru%s",
                        article.getElementsByClass("ttl01").get(0).child(0).attr("href"));
                String id = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
                if (reviewDao.find(id) == null) {
                    reviewDao.add(extractData(url, id));
                    ++created;
                } else
                    ++dropped;
            }
            LOG.info("RollingStone: {} created, {} dropped", created, dropped);
        }
    }

    @Override
    public List<Review> getLatest(int limit) {
        return reviewDao.getLatest(limit);
    }

    private Review extractData(String url, String id) throws IOException, ParseException {
        Connection.Response response = Jsoup.connect(url)
                .userAgent(userAgent)
                .execute();
        Document doc = response.parse();

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("<p><strong>Оценка: </strong>%s</p>",
                formatRating(
                        doc.getElementsByClass("block-review-inner").get(0).children())));
        for (Element el : doc.getElementsByClass("block-img-osn").get(0).children())
            builder.append(el.getElementsByTag("img").get(0));
        builder.append(doc.getElementsByClass("block-content").get(0).html());

        return new Review(
                id,
                url,
                doc.getElementsByClass("block-root").get(0).children().get(1).text(),
                builder.toString(),
                doc.getElementsByClass("block-data-author").get(0).children().get(2).text(),
                new SimpleDateFormat("dd MMMM yyyy", DF_SYM)
                        .parse(doc.getElementsByClass("block-data-author").get(0).children().get(0).text())
        );
    }
}

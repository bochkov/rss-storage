package com.sergeybochkov.rss.rollingstone.service;

import com.sergeybochkov.rss.rollingstone.dao.ReviewDao;
import com.sergeybochkov.rss.rollingstone.domain.Review;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger logger = Logger.getLogger(ReviewServiceImpl.class.getName());

    private final ReviewDao reviewDao;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    @Override
    public Review find(String id) {
        return reviewDao.find(id);
    }

    @Override
    public void add(Review review) {
        reviewDao.add(review);
    }

    private Date formatDate(String value) throws ParseException {
        List<String> months = Arrays.asList("Января", "Февраля", "Марта", "Апреля", "Мая", "Июня",
                "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря");
        for (String month : months)
            value = value.replace(month, String.valueOf(months.indexOf(month) + 1));
        DateFormat df = new SimpleDateFormat("dd MM yyyy");
        return df.parse(value);
    }

    private String formatRating(Elements stars){
        int rating = 0;
        for (Element star : stars)
            if (star.child(0).attr("src").contains("star-red.gif"))
                rating += 1;

        switch (rating) {
            case 0:
                return "Никак";
            case 1:
               return "Очень слабо";
            case 2:
                return "Неуд";
            case 3:
                return "На троечку";
            case 4:
                return "Хорошо";
            case 5:
                return "Отлично!";
        }
        return "WTF?";
    }

    @Transactional
    @Scheduled(cron="0 0 */3 * * ?")
    public void download() throws Exception {
        int created = 0;
        int dropped = 0;

        Document doc = Jsoup.connect("http://rollingstone.ru/review/")
                .userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0")
                .followRedirects(true)
                .get();
        Elements articles = doc.getElementsByClass("tbl-review-inner");
        for (Element article : articles) {
            String url = "http://rollingstone.ru" + article.getElementsByClass("ttl01").get(0).child(0).attr("href");
            String id = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
            if (find(id) == null) {
                Review rev = new Review();
                rev.setUrl(url);
                rev.setId(id);

                Document review = Jsoup.connect(url).get();

                rev.setTitle(review.getElementsByClass("block-root").get(0).children().get(1).text());
                rev.setAuthor(review.getElementsByClass("block-data-author").get(0).children().get(2).text());
                rev.setDate(formatDate(review.getElementsByClass("block-data-author").get(0).children().get(0).text()));

                Elements stars = review.getElementsByClass("block-review-inner").get(0).children();
                String text = "<p><strong>Оценка: </strong>" + formatRating(stars) + "</p>";
                Elements elem = review.getElementsByClass("block-img-osn").get(0).children();
                for (Element el : elem)
                    text += el.getElementsByTag("img").get(0);
                text += review.getElementsByClass("block-content").get(0).html();
                rev.setText(text);

                add(rev);
                ++created;
            }
            else
                ++dropped;
        }

        logger.info(String.format("RollingStone: %s created, %s dropped", created, dropped));
    }

    @Override
    public List<Review> getLatest() {
        return reviewDao.getLatest();
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void clean() {
        // todo clean old records
    }
}

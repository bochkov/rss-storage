package com.sergeybochkov.rss.itstudent.service;

import com.sergeybochkov.rss.itstudent.dao.PostDao;
import com.sergeybochkov.rss.itstudent.domain.Post;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostDao postDao;

    private static final String url = "http://it-students.net";

    @Override
    public List<Post> getLatest() {
        return postDao.getLatest();
    }

    @Transactional
    public void download() throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0")
                .followRedirects(true)
                .get();
        Element mainBlock = doc.getElementById("block-system-main");
        Elements elements = mainBlock.getElementsByClass("views-row");
        Collections.reverse(elements);
        for (Element element : elements)
            handlePost(element);
    }

    private void handlePost(Element element){
        Element elem = element.getElementsByTag("h2").get(0);
        String link = elem.child(0).attr("href");
        if (postDao.findByUrl(url + link) == null) {
            String title = elem.child(0).text();

            Elements elems = element.getElementsByClass("field-items");

            ArrayList<String> tags = new ArrayList<>();
            if (elem.children().size() > 0)
                for (Element e : elems.get(0).children())
                    tags.add(e.text());

            String text = "";
            if (elems.size() > 1)
                for (Element e : elems.get(1).children())
                    text += e.text();

            Post post = new Post();
            post.setTitle(title);
            post.setUrl(url + link);
            post.setText(text);
            post.setTags(tags);
            post.setTimestamp(System.currentTimeMillis());

            postDao.add(post);
        }
    }

    public void clean() {
        // todo clean old records
    }
}

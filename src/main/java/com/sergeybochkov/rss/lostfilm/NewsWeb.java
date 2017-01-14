package com.sergeybochkov.rss.lostfilm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/lostfilm/")
public final class NewsWeb {

    private final NewsService newsService;
    private final RssFeed rssFeed;

    @Autowired
    public NewsWeb(NewsService newsService, @Qualifier("lostfilm_rss") RssFeed rssFeed) {
        this.rssFeed = rssFeed;
        this.newsService = newsService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("objects", newsService.getLatest());
        return "lostfilm/detail";
    }

    @RequestMapping(value = "/rss/", method = RequestMethod.GET)
    public ModelAndView rss() {
        ModelAndView modelAndView = new ModelAndView(rssFeed);
        modelAndView.addObject("feed", newsService.getLatest());
        return modelAndView;
    }
}

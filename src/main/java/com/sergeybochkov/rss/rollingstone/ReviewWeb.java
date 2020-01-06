package com.sergeybochkov.rss.rollingstone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/rolling-stone/")
public final class ReviewWeb {

    private final ReviewService reviewService;
    private final RssFeed rssFeed;

    @Autowired
    public ReviewWeb(ReviewService reviewService, @Qualifier("rs_rss") RssFeed rssFeed) {
        this.reviewService = reviewService;
        this.rssFeed = rssFeed;
    }

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("rollingstone/detail");
        modelAndView.addObject("objects", reviewService.getLatest(20));
        return modelAndView;
    }

    @GetMapping("/rss/")
    public ModelAndView rss() {
        ModelAndView modelAndView = new ModelAndView(rssFeed);
        modelAndView.addObject("feed", reviewService.getLatest(20));
        return modelAndView;
    }

}

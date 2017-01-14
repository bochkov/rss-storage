package com.sergeybochkov.rss.radioutkin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/radioutkin/")
public final class QaWeb {

    private final QaService qaService;
    private final RssFeed rssFeed;

    @Autowired
    public QaWeb(QaService qaService, @Qualifier(value = "radioutkin_rss") RssFeed rssFeed) {
        this.rssFeed = rssFeed;
        this.qaService = qaService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("radioutkin/detail");
        modelAndView.addObject("objects", qaService.getLatest(50));
        return modelAndView;
    }

    @RequestMapping(value = "/rss/", method = RequestMethod.GET)
    public ModelAndView rss() {
        ModelAndView modelAndView = new ModelAndView(rssFeed);
        modelAndView.addObject("feed", qaService.getLatest(50));
        return modelAndView;
    }
}

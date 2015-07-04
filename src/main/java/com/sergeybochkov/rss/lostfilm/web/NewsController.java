package com.sergeybochkov.rss.lostfilm.web;

import com.sergeybochkov.rss.lostfilm.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/lostfilm/")
public class NewsController {

    @Autowired
    private NewsService newsService;
    @Autowired
    @Qualifier("lostfilm_rss")
    private RssViewer rssViewer;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("objects", newsService.getLatest());
        return "lostfilm/detail";
    }

    @RequestMapping(value = "/rss/", method = RequestMethod.GET)
    public ModelAndView rss() {
        ModelAndView modelAndView = new ModelAndView(rssViewer);
        modelAndView.addObject("feed", newsService.getLatest());
        return modelAndView;
    }
}

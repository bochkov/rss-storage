package com.sergeybochkov.rss.lostfilm.web;

import com.sergeybochkov.rss.lostfilm.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/lostfilm/")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("objects", newsService.getLatest());
        return "lostfilm/detail";
    }

    @RequestMapping(value = "/rss/", method = RequestMethod.GET)
    public String rss(Model model){
        model.addAttribute("feed", newsService.getLatest());
        return "lostfilm_rss";
    }
}

package com.sergeybochkov.rss.andronov.web;

import com.sergeybochkov.rss.andronov.service.AndQaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/andronov/")
public class AndQaController {

    @Autowired
    private AndQaService service;
    @Autowired
    @Qualifier("andronov_rss")
    private RssViewer rssViewer;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("andronov/detail");
        modelAndView.addObject("objects", service.getLatest());
        return modelAndView;
    }

    @RequestMapping(value = "/rss/", method = RequestMethod.GET)
    public ModelAndView rss() {
        ModelAndView modelAndView = new ModelAndView(rssViewer);
        modelAndView.addObject("feed", service.getLatest());
        return modelAndView;
    }
}

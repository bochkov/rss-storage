package com.sergeybochkov.rss.radioutkin.web;

import com.sergeybochkov.rss.radioutkin.service.QaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/radioutkin/")
public class QaController {

    private final QaService qaService;
    private final RssViewer rssViewer;

    @Autowired
    public QaController(QaService qaService, @Qualifier(value = "radioutkin_rss") RssViewer rssViewer) {
        this.rssViewer = rssViewer;
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
        ModelAndView modelAndView = new ModelAndView(rssViewer);
        modelAndView.addObject("feed", qaService.getLatest(100));
        return modelAndView;
    }

}

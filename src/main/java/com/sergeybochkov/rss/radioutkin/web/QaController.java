package com.sergeybochkov.rss.radioutkin.web;

import com.sergeybochkov.rss.radioutkin.service.QaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/radioutkin/")
public class QaController {

    @Autowired
    private QaService qaService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("radioutkin/detail");
        modelAndView.addObject("objects", qaService.getLatest());
        return modelAndView;
    }

    @RequestMapping(value = "/rss/", method = RequestMethod.GET)
    public ModelAndView rss(){
        ModelAndView modelAndView = new ModelAndView("radioutkin_rss");
        modelAndView.addObject("feed", qaService.getLatest());
        return modelAndView;
    }

}

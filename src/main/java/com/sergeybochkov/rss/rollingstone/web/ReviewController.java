package com.sergeybochkov.rss.rollingstone.web;

import com.sergeybochkov.rss.rollingstone.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/rolling-stone/")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView("rollingstone/detail");
        modelAndView.addObject("objects", reviewService.getLatest());
        return modelAndView;
    }

    @RequestMapping(value = "/rss/", method = RequestMethod.GET)
    public ModelAndView rss(){
        ModelAndView modelAndView = new ModelAndView("rs_rss");
        modelAndView.addObject("feed", reviewService.getLatest());
        return modelAndView;
    }

}

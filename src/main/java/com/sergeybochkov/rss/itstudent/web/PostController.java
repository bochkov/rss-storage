package com.sergeybochkov.rss.itstudent.web;

import com.sergeybochkov.rss.itstudent.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("it-students")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    @Qualifier(value = "itstudents_rss")
    private RssViewer rssViewer;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView("itstudent/detail");
        modelAndView.addObject("objects", postService.getLatest());
        return modelAndView;
    }

    @RequestMapping(value = "/rss/", method = RequestMethod.GET)
    public ModelAndView rss(){
        ModelAndView modelAndView = new ModelAndView(rssViewer);
        modelAndView.addObject("feed", postService.getLatest());
        return modelAndView;
    }
}

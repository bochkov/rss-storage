package com.sergeybochkov.rss.itstudent.web;

import com.sergeybochkov.rss.itstudent.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("it-students")
public class PostController {

    @Autowired
    private PostService postService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView("itstudent/detail");
        modelAndView.addObject("objects", postService.getLatest());
        return modelAndView;
    }

    @RequestMapping(value = "/rss/", method = RequestMethod.GET)
    public ModelAndView rss(){
        ModelAndView modelAndView = new ModelAndView("it_rss");
        modelAndView.addObject("feed", postService.getLatest());
        return modelAndView;
    }
}

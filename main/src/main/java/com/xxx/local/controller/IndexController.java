package com.xxx.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author xiaobaoqiu  Date: 17-1-19 Time: 下午3:06
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/noAuth", method = RequestMethod.GET)
    public ModelAndView auth() {
        return new ModelAndView("noauth");
    }

    @RequestMapping(value = "/500", method = RequestMethod.GET)
    public ModelAndView error() {
        return new ModelAndView("500");
    }
}
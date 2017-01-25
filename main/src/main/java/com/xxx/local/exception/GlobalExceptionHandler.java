package com.xxx.local.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GlobalExceptionHandler implements HandlerExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static MappingJackson2JsonView jsonView = new MappingJackson2JsonView();

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String uri = request.getRequestURI();
        ModelAndView mv = new ModelAndView();
        if (ex instanceof RuntimeException) {
            logger.error("error uri: {}", uri, ex);
            mv.setView(jsonView);
            mv.addObject("status", -1);
            mv.addObject("message", ex.getMessage());
        } else {
            logger.error("unexcepted error uri: {}", uri, ex);
            mv.setViewName("500");
            mv.addObject("message", ex.getMessage());
            mv.addObject("e", ex);
        }
        return mv;
    }
}

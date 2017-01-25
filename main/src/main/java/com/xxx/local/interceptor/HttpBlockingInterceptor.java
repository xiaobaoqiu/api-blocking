package com.xxx.local.interceptor;


import com.xxx.local.service.blocking.BlockingManager;
import com.xxx.local.service.blocking.result.BlockingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xiaobaoqiu  Date: 17-1-23 Time: 下午4:10
 */
public class HttpBlockingInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(HttpBlockingInterceptor.class);

    @Resource
    private BlockingManager blockingManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        BlockingResult result = blockingManager.isBlocked(request);
        if (result.isBlocked()) {   //被阻塞
            logger.warn("请求被阻塞,uri={}", request.getRequestURI());
            response.sendRedirect(result.getConfig().getRedirectUrl());
            return false;
        }

        logger.info("ok,uri={}", request.getRequestURI());
        return true;
    }
}

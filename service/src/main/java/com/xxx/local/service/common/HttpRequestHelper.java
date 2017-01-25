package com.xxx.local.service.common;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiaobaoqiu  Date: 17-1-19 Time: 下午4:45
 */
public final class HttpRequestHelper {

    /**
     * 获取客户端的IP地址
     */
    public static String getClientIP(HttpServletRequest request) {
        /*
         * X-Forwarded-For存储了从客户端到Tomcat包含中间所有Nginx的IP地址，使用逗号分隔，例如
         * <client_ip>, <nginx1_ip>, <nginx2_ip>
         * client_ip可以伪造，但此处我们只需要保证对于用户的两次请求使用的是同一个IP即可
         *
         * X-Real-IP存储的是客户端/上一个Nginx的地址
         */
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip)) {
            ip = request.getHeader("X-Real-IP");
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();
            }
        }

        return StringUtils.substringBefore(ip, ",");
    }
}

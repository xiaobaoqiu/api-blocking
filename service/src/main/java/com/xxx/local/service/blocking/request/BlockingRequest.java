package com.xxx.local.service.blocking.request;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 请求封装
 *
 * @author xiaobaoqiu  Date: 17-1-19 Time: 下午9:07
 */
public class BlockingRequest implements Serializable {

    /**
     * 原始的请求
     */
    private HttpServletRequest originalRequest;

    /**
     * 请求端IP
     */
    private String clientIp;

    /**
     * 请求事件
     */
    private long requestMilisecond;

    public HttpServletRequest getOriginalRequest() {
        return originalRequest;
    }

    public void setOriginalRequest(HttpServletRequest originalRequest) {
        this.originalRequest = originalRequest;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public long getRequestMilisecond() {
        return requestMilisecond;
    }

    public void setRequestMilisecond(long requestMilisecond) {
        this.requestMilisecond = requestMilisecond;
    }
}

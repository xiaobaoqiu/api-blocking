package com.xxx.local.service.blocking.config;

import com.xxx.local.service.common.PrintableBean;

import java.io.Serializable;

/**
 * @author xiaobaoqiu  Date: 17-1-19 Time: 下午8:58
 */
public class BlockingConfig extends PrintableBean implements Serializable {

    /**
     * 待阻塞的 url
     */
    private String url;

    private long duration;

    private int limit;

    /**
     * 超额之后跳转的url
     */
    private String redirectUrl;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}

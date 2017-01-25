package com.xxx.local.service.blocking.counter;

import com.xxx.local.service.blocking.config.BlockingConfig;
import com.xxx.local.service.blocking.request.BlockingRequest;

/**
 * @author xiaobaoqiu  Date: 17-1-24 Time: 下午4:30
 */
public interface Counter {

    /**
     * 请求是否已经超出限制
     *
     * @param request 请求
     * @param config  请求对应的配置
     */
    boolean exceed(final BlockingRequest request, final BlockingConfig config);
}

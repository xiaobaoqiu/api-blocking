package com.xxx.local.service.blocking.counter.ratelimit;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.xxx.local.service.blocking.config.BlockingConfig;
import com.xxx.local.service.blocking.counter.Counter;
import com.xxx.local.service.blocking.request.BlockingRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 使用 Guava 的 RateLimit 实现限流
 *
 * @author xiaobaoqiu  Date: 17-1-25 Time: 上午11:04
 */
@Service("rateLimitCounter")
public class RateLimitCounter implements Counter {

    private Map<String, RateLimiter> requestCounter;

    public RateLimitCounter() {
        requestCounter = Maps.newHashMap();
    }

    @Override
    public boolean exceed(BlockingRequest request, BlockingConfig config) {
        Preconditions.checkArgument(request != null && config != null);
        String key = buildKey(request);

        RateLimiter counter = requestCounter.get(key);
        // 初始化
        if (counter == null) {
            counter = RateLimiter.create(config.getLimit() * 1.0 / config.getDuration());
            requestCounter.put(key, counter);
        }

        return !counter.tryAcquire();
    }

    private String buildKey(final BlockingRequest request) {
        return request.getOriginalRequest().getRequestURI()
                + request.getClientIp();
    }
}

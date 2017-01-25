package com.xxx.local.service.blocking.counter.slidingwindow;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.xxx.local.service.blocking.config.BlockingConfig;
import com.xxx.local.service.blocking.counter.Counter;
import com.xxx.local.service.blocking.request.BlockingRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 使用滑动窗口作为计数器使用
 *
 * @author xiaobaoqiu  Date: 17-1-24 Time: 下午3:21
 */
@Service("slidingWindowCounter")
public class SlidingWindowCounter implements Counter {

    private Map<String, CircularQueue> requestCounter;
    private Map<String, LifeCyclePredicate> lifeCyclePredicateMap;

    SlidingWindowCounter() {
        requestCounter = Maps.newHashMap();
        lifeCyclePredicateMap = Maps.newHashMap();
    }

    @Override
    public boolean exceed(final BlockingRequest request, final BlockingConfig config) {
        Preconditions.checkArgument(request != null && config != null);
        String key = buildKey(request);

        CircularQueue counter = requestCounter.get(key);
        // 初始化
        if (counter == null) {
            counter = new CircularQueue(config.getLimit());
            requestCounter.put(key, counter);
            counter.enqueue(request.getRequestMilisecond(), getLifeCyclePredicate(config));
            return false;
        }

        // 队列满 且 队首元素(最早的一个元素)没有过期,说明超出期限了
        if (counter.isFull() && !getLifeCyclePredicate(config).apply(counter.head())) {
            return true;
        }

        return !counter.enqueue(request.getRequestMilisecond(), getLifeCyclePredicate(config));
    }

    private String buildKey(final BlockingRequest request) {
        return request.getOriginalRequest().getRequestURI()
                + request.getClientIp();
    }

    private LifeCyclePredicate getLifeCyclePredicate(final BlockingConfig config) {
        LifeCyclePredicate predicate = lifeCyclePredicateMap.get(config.getUrl());
        if (predicate == null) {
            predicate = new LifeCyclePredicate(config.getDuration() * 1000L);   //second --> millisecond
            lifeCyclePredicateMap.put(config.getUrl(), predicate);
        }
        return predicate;
    }
}

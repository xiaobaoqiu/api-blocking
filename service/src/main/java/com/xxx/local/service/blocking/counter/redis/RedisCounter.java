package com.xxx.local.service.blocking.counter.redis;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.xxx.local.service.blocking.BlockingConstants;
import com.xxx.local.service.blocking.config.BlockingConfig;
import com.xxx.local.service.blocking.counter.Counter;
import com.xxx.local.service.blocking.request.BlockingRequest;
import com.xxx.local.service.redis.RedisClientTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 使用 Redis 作为计数器使用
 *
 * @author xiaobaoqiu  Date: 17-1-23 Time: 下午3:50
 */
@Service("redisCounter")
public class RedisCounter implements Counter {

    @Resource
    private RedisClientTemplate redisClientTemplate;

    /**
     * 请求是否已经超出限制
     */
    @Override
    public boolean exceed(final BlockingRequest request, final BlockingConfig config) {
        Preconditions.checkArgument(request != null && config != null);
        String key = buildRedisKey(request);

        String count = redisClientTemplate.get(key);
        if (count != null) {
            Integer c = Ints.tryParse(count);
            if (c != null && c >= config.getLimit()) {  //超出配置的请求数目
                return true;
            }
        }

        Long result = redisClientTemplate.increase(key);
        if (result == 1L) {
            redisClientTemplate.expire(key, (int) config.getDuration());
        }
        return false;
    }

    /**
     * 构建 redis 的 key
     */
    private String buildRedisKey(BlockingRequest request) {
        return BlockingConstants.BLOCKING_REDIS_PREFIX
                + request.getOriginalRequest().getRequestURI()
                + request.getClientIp();
    }
}

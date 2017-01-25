package com.xxx.local.service.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

/**
 * @author xiaobaoqiu  Date: 17-1-23 Time: 下午3:40
 */
@Service
public class RedisClientTemplate {

    private static final Logger logger = LoggerFactory.getLogger(RedisClientTemplate.class);

    @Resource
    private RedisDataSource redisDataSource;

    /**
     * 获取单个值
     */
    public String get(String key) {
        ShardedJedis shardedJedis = redisDataSource.getRedisClient();
        String result = null;
        try {
            result = shardedJedis.get(key);
        } catch (Exception e) {
            logger.error("Redis get异常,key={}", key, e);
        } finally {
            redisDataSource.returnResource(shardedJedis);
        }
        return result;
    }

    /**
     * 在某段时间后失效
     */
    public Long expire(String key, int seconds) {
        ShardedJedis shardedJedis = redisDataSource.getRedisClient();
        Long result = null;
        try {
            result = shardedJedis.expire(key, seconds);
        } catch (Exception e) {
            logger.error("Redis expire异常,key={}", key, e);
        } finally {
            redisDataSource.returnResource(shardedJedis);
        }
        return result;
    }

    public Long increase(String key) {
        ShardedJedis shardedJedis = redisDataSource.getRedisClient();
        Long result = null;
        try {
            result = shardedJedis.incr(key);
        } catch (Exception e) {
            logger.error("Redis increase异常,key={}", key, e);
        } finally {
            redisDataSource.returnResource(shardedJedis);
        }
        return result;
    }
}

package com.xxx.local.service.redis;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;

/**
 * @author xiaobaoqiu  Date: 17-1-23 Time: 下午3:38
 */
@Service
public class RedisDataSource {

    private static final Logger logger = LoggerFactory.getLogger(RedisDataSource.class);

    @Resource
    private ShardedJedisPool shardedJedisPool;

    public ShardedJedis getRedisClient() {
        ShardedJedis shardJedis;
        try {
            shardJedis = shardedJedisPool.getResource();
            Preconditions.checkNotNull(shardJedis, "获取ShardedJedis失败");

            return shardJedis;
        } catch (Exception e) {
            logger.error("getRedisClent error, ", e);
            return null;
        }
    }

    public void returnResource(ShardedJedis shardedJedis) {
        shardedJedis.close();
    }
}

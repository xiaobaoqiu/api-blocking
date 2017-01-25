# 1.Spring Api Blocking
这是一个 Api Blocking(API 限流) 的Demo代码,包括使用两种途径:

    (1).Redis
    (2).滑动窗口
    (3).RateLimit
    
其中 Redis 更试用全局的Api Blocking,后两种方式是单机版的.

# 2.如何试用
试用方式

    (0).启动Redis(可选)
    (1).配置 blocking-config.properties
    (2).在 ApplicationResources.properties 文件中配置 Blocking 策略, 可选值: redisCounter,slidingWindowCounter,rateLimitCounter
    (3).启动应用

# 3.实现方式
大致的原理:

    (1).请求拦截器 HttpBlockingInterceptor 拦截所有的 http 请求
    (2).核心代码入口在 BlockingManager

package com.xxx.local.service.blocking;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.xxx.local.service.blocking.config.BlockingConfig;
import com.xxx.local.service.blocking.counter.Counter;
import com.xxx.local.service.blocking.request.BlockingRequest;
import com.xxx.local.service.blocking.result.BlockingResult;
import com.xxx.local.service.common.HttpRequestHelper;
import com.xxx.local.service.common.PropertyUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author xiaobaoqiu  Date: 17-1-20 Time: 下午3:35
 */
@Service
public class BlockingManager implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(BlockingManager.class);

    /**
     * 默认的配置文件命名
     */
    private static final String GLOBAL_CONFIG_PATH = "blocking-config.properties";
    private static final String CONFIG_COUNTER_SEPARATOR = ".";

    /**
     * 策略
     */
    private static final String BLOCKING_STRATEGY = PropertyUtil.getProperty("blocking.strategy", "redisCounter");

    Map<String, BlockingConfig> configMap;

    private Counter counter;

    public BlockingResult isBlocked(final HttpServletRequest request) {
        Preconditions.checkNotNull(counter, "counter初始化失败");
        BlockingRequest blockingRequest = buildBlockingRequest(request);
        BlockingConfig config = configMap.get(request.getRequestURI());
        boolean exceeded = exceed(blockingRequest, config);

        BlockingResult result = new BlockingResult();
        result.setIsBlocked(exceeded);
        result.setConfig(config);

        return result;
    }

    private boolean exceed(BlockingRequest request, BlockingConfig config) {
        return config != null && counter.exceed(request, config);
    }

    /**
     * 读取配置文件
     *
     * @return key-表示块的数字, value-表示块内的配置
     */
    private Map<String, Map<String, String>> readConfig() {
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties(GLOBAL_CONFIG_PATH);
            Set<String> keys = properties.stringPropertyNames();

            Map<String, Map<String, String>> groupMap = Maps.newHashMap();

            String prefix;
            for (String key : keys) {
                int pos = key.indexOf(CONFIG_COUNTER_SEPARATOR);
                Preconditions.checkArgument(pos != -1, "配置错误");

                prefix = key.substring(0, pos);
                Preconditions.checkArgument(StringUtils.isNotEmpty(prefix), "配置错误");

                Map<String, String> configs = groupMap.get(prefix);
                if (configs == null) {
                    configs = Maps.newHashMap();
                    groupMap.put(prefix, configs);
                }
                configs.put(key.substring(pos + 1), properties.getProperty(key));
            }
            return groupMap;
        } catch (Exception e) {
            logger.error("读取配置文件失败,filename={}", GLOBAL_CONFIG_PATH);
            return null;
        }
    }

    private void initConfig() {
        loadWebConfig(readConfig());
    }

    /**
     * 解析配置文件
     */
    private void loadWebConfig(final Map<String, Map<String, String>> configs) {
        //1.每组构造一个 DefaultBlockingConfig
        configMap = Maps.newHashMapWithExpectedSize(configs.size());
        for (Map.Entry<String, Map<String, String>> e : configs.entrySet()) {
            BlockingConfig config = buildBlockingConfig(e.getValue());
            if (config == null) {
                logger.error("解析配置失败,prefix={},配置信息={}", e.getKey(), e.getValue());
                continue;
            }

            configMap.put(config.getUrl(), config);
        }
    }

    private BlockingConfig buildBlockingConfig(Map<String, String> configMap) {
        if (MapUtils.isEmpty(configMap)) {
            return null;
        }

        String url = MapUtils.getString(configMap, BlockingConstants.BLOCKING_CONFIG_URL_KEY, StringUtils.EMPTY);
        String redirectUrl = MapUtils.getString(configMap, BlockingConstants.BLOCKING_CONFIG_REDIRECTURL_KEY, StringUtils.EMPTY);
        long duration = MapUtils.getLong(configMap, BlockingConstants.BLOCKING_CONFIG_DURATION_KEY, -1L);
        int limit = MapUtils.getInteger(configMap, BlockingConstants.BLOCKING_CONFIG_LIMIT_KEY, -1);

        Preconditions.checkArgument(StringUtils.isNotEmpty(url), "name配置必须");
        Preconditions.checkArgument(StringUtils.isNotEmpty(redirectUrl), "redirectUrl配置必须");
        Preconditions.checkArgument(duration > 0, "duration配置必须");
        Preconditions.checkArgument(limit > 0, "limit配置必须");

        BlockingConfig config = new BlockingConfig();
        config.setUrl(url);
        config.setRedirectUrl(redirectUrl);
        config.setDuration(duration);
        config.setLimit(limit);

        return config;
    }

    private BlockingRequest buildBlockingRequest(final HttpServletRequest request) {
        BlockingRequest blockingRequest = new BlockingRequest();
        blockingRequest.setOriginalRequest(request);
        blockingRequest.setClientIp(HttpRequestHelper.getClientIP(request));
        blockingRequest.setRequestMilisecond(System.currentTimeMillis());

        return blockingRequest;
    }

    private void initCounter(ApplicationContext applicationContext) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(BLOCKING_STRATEGY), "Blocking策略不能为空");
        logger.info("BLOCKING_STRATEGY={}", BLOCKING_STRATEGY);

        counter = Preconditions.checkNotNull(
                applicationContext.getBean(BLOCKING_STRATEGY, Counter.class),
                "获取Counter失败, BLOCKING_STRATEGY=%s", BLOCKING_STRATEGY);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 加载配置
        initConfig();

        // blocking策略
        initCounter(applicationContext);
    }
}

package com.xxx.local.service.blocking.result;

import com.xxx.local.service.blocking.config.BlockingConfig;

import java.io.Serializable;

public class BlockingResult implements Serializable {

    public static final BlockingResult NOT_BLOCK;

    static {
        NOT_BLOCK = new BlockingResult();
        NOT_BLOCK.setIsBlocked(false);
    }

    /**
     * 是否阻塞
     */
    private boolean isBlocked;

    /**
     * 配置
     */
    private BlockingConfig config;

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public void setConfig(BlockingConfig config) {
        this.config = config;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public BlockingConfig getConfig() {
        return config;
    }
}

package com.xxx.local.service.common;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author xiaobaoqiu  Date: 17-1-24 Time: 下午2:36
 */
public abstract class PrintableBean {

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

package com.xxx.local.service.blocking.counter.slidingwindow;

import com.google.common.base.Predicate;

/**
 * 用于判断元素是否过期
 *
 * @author xiaobaoqiu  Date: 17-1-24 Time: 下午6:47
 */
class LifeCyclePredicate implements Predicate<Long> {

    private long lifeCycle;

    public LifeCyclePredicate(Long lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    @Override
    public boolean apply(Long input) {
        return System.currentTimeMillis() - input > this.lifeCycle;
    }
}

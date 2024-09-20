package org.idea.livestream.framework.redis.starter.Key;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author dream
 * @data 2024/9/19 下午4:01
 * @descripation
 */
public class RedisKeyBuilder {
    @Value("${spring.application.name}")
    private String applicationName;
    private static final String SPLIT_ITEM = ":";

    public String getSplitItem() {
        return SPLIT_ITEM;
    }

    public String getPrefix() {
        return applicationName + SPLIT_ITEM;
    }
}

package org.idea.livestream.framework.redis.starter.Key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author dream
 * @data 2024/9/19 下午4:06
 * @descripation
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class UserProviderCacheKeyBuilder extends RedisKeyBuilder{

    private static String USER_INFO_KEY = "userInfo";

    /**
     * 构建用户信息key
     * @param userId
     * @return
     */
    public String bulidUserInfoKey(Long userId) {
        return super.getPrefix() + USER_INFO_KEY + super.getSplitItem() + userId;
    }

}

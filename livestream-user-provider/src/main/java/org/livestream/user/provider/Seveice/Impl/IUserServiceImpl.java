package org.livestream.user.provider.Seveice.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.idea.livestream.framework.redis.starter.Key.UserProviderCacheKeyBuilder;
import org.livestream.common.Interfaces.ConvertBeanUtils;
import org.livestream.user.Dto.UserDto;
import org.livestream.user.provider.Dao.po.UserPO;
import org.livestream.user.provider.Seveice.IUserService;
import org.livestream.user.provider.Dao.mapper.IUserMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author dream
 * @data 2024/9/17 下午8:53
 * @descripation
 */
@Service
public class IUserServiceImpl implements IUserService {
    @Resource
    private IUserMapper userMapper;
    @Resource
    private RedisTemplate<String,UserDto> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;
    @Resource
    private MQProducer mqProducer;

    /**
     * 根据用户id获取用户信息
     * @param userId
     * @return
     */
    @Override
    public UserDto getUserInfoById(Long userId) {
        if(userId == null) {
            return null;
        }
        String key = userProviderCacheKeyBuilder.bulidUserInfoKey(userId);
        UserDto userDto = redisTemplate.opsForValue().get(key);
        if(userDto != null) {
            return userDto;
        }
        userDto = ConvertBeanUtils.convert(userMapper.selectById(userId), UserDto.class);
        if (userDto != null) {
            redisTemplate.opsForValue().set(key, userDto,30, TimeUnit.MINUTES);
        }
        return userDto;
    }

    /**
     * 批量获取用户信息
     * @param userIds
     * @return
     */
    @Override
    public Map<Long, UserDto> batchQueryUserInfo(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)) {
            return Maps.newHashMap();
        }
        userIds = userIds.stream().filter(Objects::nonNull).toList();
        if (CollectionUtils.isEmpty(userIds)) {
            return Maps.newHashMap();
        }

        //redis
        List<String> keyList = new ArrayList<>();
        //将所有的key放入list
        userIds.forEach(userId -> {
            keyList.add(userProviderCacheKeyBuilder.bulidUserInfoKey(userId));
        });
        List<UserDto> userDtoList = Objects.requireNonNull(redisTemplate.opsForValue().multiGet(keyList)).stream().
                filter(Objects::nonNull).collect(Collectors.toList());
        //全部在缓存中
        if (!CollectionUtils.isEmpty(userDtoList) && userDtoList.size() == userIds.size()) {
            return userDtoList.stream().collect(Collectors.toMap(UserDto::getUserId, userDto -> userDto));
        }
        //将缓存中有的key放入list
        List<Long> userIdInCacheList = userDtoList.stream().map(UserDto::getUserId).toList();
        //将缓存中没有的key放入list
        List<Long> userIdNotInCacheList = userIds.stream().filter(userId -> !userIdInCacheList.contains(userId)).toList();



        //多线程查询 替换了union all

        //先本地归并  100个一组
        Map<Long, List<Long>> userIdsMap = userIdNotInCacheList.stream().collect(Collectors.groupingBy(userId -> userId % 100));
        List<UserDto> dbQueryResult = Collections.synchronizedList(new ArrayList<>());
        //使用并行流
        userIdsMap.values().parallelStream().forEach(ids -> {
            dbQueryResult.addAll(ConvertBeanUtils.convertList(userMapper.selectBatchIds(ids), UserDto.class));
        });

        //将查询结果放入缓存
        Map<String,UserDto> saveCacheMap = dbQueryResult.stream().
                collect(Collectors.toMap(userDto -> userProviderCacheKeyBuilder.bulidUserInfoKey(userDto.getUserId()), userDto -> userDto));
        if(!CollectionUtils.isEmpty(saveCacheMap)) {
            redisTemplate.opsForValue().multiSet(saveCacheMap);
            //管道批量传输数据、减少网络IO开销
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    for(String redisKey : saveCacheMap.keySet()) {
                        operations.expire((K) redisKey,30,TimeUnit.MINUTES);
                        
                    }
                    return null;
                }
            });
            userDtoList.addAll(dbQueryResult);
        }

        //转换成map
        return userDtoList.stream().collect(Collectors.toMap(UserDto::getUserId, userDto -> userDto));
    }

    /**
     * 更新用户信息
     * @param userDto
     * @return
     */
    @Override
    public boolean updateUser(UserDto userDto) {
        if(userDto == null || userDto.getUserId() == null) {
            return false;
        }
        userMapper.updateById(ConvertBeanUtils.convert(userDto, UserPO.class));
        redisTemplate.delete(userProviderCacheKeyBuilder.bulidUserInfoKey(userDto.getUserId()));
        try {
            Message message = new Message();
            message.setBody(JSON.toJSONString(userDto).getBytes());
            message.setTopic("user-update-cache");
            //延迟1s
            message.setDelayTimeLevel(1);
            mqProducer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * 插入用户信息
     * @param userDto
     * @return
     */
    @Override
    public boolean insertUser(UserDto userDto) {
        if(userDto == null || userDto.getUserId() == null) {
            return false;
        }
        userMapper.insert(ConvertBeanUtils.convert(userDto, UserPO.class));

        return true;
    }

    /**
     * 删除用户信息
     * @param userIds
     * @return
     */
    @Override
    public boolean deleteUser(List userIds) {
        if (userIds == null || userIds.size() == 0) {
            return false;
        }
        userMapper.deleteBatchIds(userIds);
        return true;
    }
}

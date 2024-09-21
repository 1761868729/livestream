package org.livestream.user.provider.Config;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.idea.livestream.framework.redis.starter.Key.UserProviderCacheKeyBuilder;
import org.livestream.user.Dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Objects;


/**
 * RocketMQ消费者配置类
 * @author dream
 * @data 2024/9/20 下午6:00
 * @descripation
 */
@Configuration
public class RocketMQConsumerConfig implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQConsumerConfig.class);

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private RedisTemplate<String, Objects> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化消费者
        initConsumer();
    }

//    DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
//    mqPushConsumer.setVipChannelEnabled(false);
//    mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameServer());
//    mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName()+"_"+RocketMQConsumerConfig.class.getSimpleName());
//    mqPushConsumer.setConsumeMessageBatchMaxSize(1);
//    mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
//    mqPushConsumer.subscribe("UserInfoCacheDelete","");
//    mqPushConsumer.setMessageListener(new MessageListenerConcurrently() {
//    @Override
//    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
//            LOGGER.info("userInfo is {}",new String(msgs.get(0).getBody()));
//            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//    }
//    });
//    mqPushConsumer.start();

    public void initConsumer(){
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();
        defaultMQPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameServer());
        defaultMQPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName());
        defaultMQPushConsumer.setConsumeMessageBatchMaxSize(1);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        try {
            defaultMQPushConsumer.subscribe("user-update-cache", "*");
            defaultMQPushConsumer.setMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                    String msgStr = new String(list.get(0).getBody());
                    UserDto userDto = JSON.parseObject(msgStr, UserDto.class);
                    if (userDto == null || userDto.getUserId() == null){
                        LOGGER.info("用户id为空，参数异常，内容：{}",msgStr);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    redisTemplate.delete(userProviderCacheKeyBuilder.bulidUserInfoKey(userDto.getUserId()));
                    LOGGER.info("延迟删除处理 uerDto is {}",userDto);
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            defaultMQPushConsumer.start();
            LOGGER.info("mq消费者启动成功,nameServer:{}" ,rocketMQConsumerProperties.getNameServer());
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
    }
}

package org.livestream.user.provider.Config;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * RocketMQ生产者配置类
 * @author dream
 * @data 2024/9/20 下午5:35
 * @descripation
 */
@Configuration
public class RocketMQProducerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQProducerConfig.class);

    @Resource
    private RocketMQProducerProperties rocketMQProducerProperties;
    @Value("$Spring.application.name")
    private String applicationName;

    @Bean
    public MQProducer mqProducer(){
        //创建线程池
        ThreadPoolExecutor RocketMQThreadPoolExecutor = new ThreadPoolExecutor(100, 150, 3,
                TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000), new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(applicationName + ":rocketmq-producer"+ ThreadLocalRandom.current().nextInt(1000));
                return thread;
            }
        });
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
        try {
            defaultMQProducer.setNamesrvAddr(rocketMQProducerProperties.getNameServer());
            defaultMQProducer.setProducerGroup(rocketMQProducerProperties.getGroupName());
            defaultMQProducer.setRetryTimesWhenSendFailed(rocketMQProducerProperties.getRetryTimes());
            defaultMQProducer.setSendMsgTimeout(rocketMQProducerProperties.getSendMsgTimeout());
            //设置发送失败后重试另外一个broker
            defaultMQProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
            //设置异步发送线程池
            defaultMQProducer.setAsyncSenderExecutor(RocketMQThreadPoolExecutor);
            defaultMQProducer.start();
            LOGGER.info("mq生产者启动成功，nameServer:{}",rocketMQProducerProperties.getNameServer());
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
        return defaultMQProducer;
    }
}

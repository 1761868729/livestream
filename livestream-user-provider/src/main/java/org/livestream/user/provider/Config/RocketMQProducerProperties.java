package org.livestream.user.provider.Config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author dream
 * @data 2024/9/20 下午5:30
 * @descripation
 */
@ConfigurationProperties(prefix = "livestream.rocketmq.producer")
@Configuration
@Data
@ToString
public class RocketMQProducerProperties {

    //rocketmq的namespace的地址
    private String nameServer;
    //生产者组名
    private String groupName;
    //消息重发次数
    private int retryTimes;
    //消息发送超时时间
    private int sendMsgTimeout;
}

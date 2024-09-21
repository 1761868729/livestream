package org.livestream.user.provider.Config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author dream
 * @data 2024/9/20 下午5:55
 * @descripation
 */
@ConfigurationProperties(prefix = "livestream.rocketmq.consumer")
@Configuration
@Data
@ToString
public class RocketMQConsumerProperties {
    //rocketmq的namespace的地址
    private String nameServer;
    //消费者组名
    private String groupName;
}

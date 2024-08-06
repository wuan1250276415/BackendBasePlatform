package pro.wuan.common.mq.config;

import lombok.Data;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 消费者配置
 * @author: oldone
 * @date: 2020/9/9 10:42
 */
@Data
@Configuration
public class MQConsumerConfig {
    @Value("${tellhow.rocketmq.name-server}")
    private String namesrvAddr;
    @Value("${tellhow.rocketmq.producer.algorithm.comsumerGroup}")
    private String groupName;
    @Value("${tellhow.rocketmq.consumer.consumeThreadMin}")
    private int consumeThreadMin;
    @Value("${tellhow.rocketmq.consumer.consumeThreadMax}")
    private int consumeThreadMax;
    @Value("${tellhow.rocketmq.consumer.consumeMessageBatchMaxSize}")
    private int consumeMessageBatchMaxSize;
    @Value("${tellhow.rocketmq.consumer.topic:default}")
    private String topic;
    @Value("${tellhow.rocketmq.consumer.message-model:CLUSTERING}")
    private MessageModel messageModel;
    @Value("${tellhow.rocketmq.consumer.reconsumeTimes}")
    private Integer reconsumeTimes;
}

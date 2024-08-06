package pro.wuan.common.mq.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.wuan.common.mq.exception.RocketMQException;

/**
 * @description: mq生产者配置类
 * @author: oldone
 * @date: 2020/9/9 10:01
 */
@Slf4j
@Configuration
public class MQProducerConfig {

    /**
     * 发送同一类消息的设置为同一个group，保证唯一,默认不需要设置，rocketmq会使用ip@pid(pid代表jvm名字)作为唯一标示
     */
    @Value("${tellhow.rocketmq.producer.topic:default}")
    private String groupName;
    /**
     * rocketmq地址
     */
    @Value("${tellhow.rocketmq.name-server}")
    private String namesrvAddr;
    /**
     * 消息最大大小，默认4M
     */
    @Value("${tellhow.rocketmq.producer.maxMessageSize}")
    private Integer maxMessageSize;
    /**
     * 消息发送超时时间，默认3秒
     */
    @Value("${tellhow.rocketmq.producer.sendMsgTimeout}")
    private Integer sendMsgTimeout;
    /**
     * 消息发送失败重试次数，默认2次
     */
    @Value("${tellhow.rocketmq.producer.retryTimesWhenSendFailed}")
    private Integer retryTimesWhenSendFailed;

    @Bean
    public DefaultMQProducer getRocketMQProducer() throws MQClientException {

        if (StringUtils.isEmpty(this.groupName)) {
            throw new RocketMQException("groupName is blank");
        }

        if (StringUtils.isEmpty(this.namesrvAddr)) {
            throw new RocketMQException("nameServerAddr is blank");
        }

        DefaultMQProducer producer;
        producer = new DefaultMQProducer(this.groupName);
        producer.setNamesrvAddr(this.namesrvAddr);

        if (this.sendMsgTimeout != null) {
            producer.setSendMsgTimeout(this.sendMsgTimeout);
        }

        if (this.maxMessageSize != null) {
            producer.setMaxMessageSize(this.maxMessageSize);
        }

        //如果发送消息失败，设置重试次数
        if (this.retryTimesWhenSendFailed != null) {
            producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
        }
        producer.setVipChannelEnabled(false);
        try {
            producer.start();
            log.info(String.format("producer is start ! groupName:[%s],namesrvAddr:[%s]"
                    , this.groupName, this.namesrvAddr));

        } catch (MQClientException e) {
            log.error(String.format("producer is error {}"
                    , e.getMessage(), e));
            throw e;

        }
        return producer;
    }


}

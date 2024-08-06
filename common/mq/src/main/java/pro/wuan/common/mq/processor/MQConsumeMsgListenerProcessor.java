package pro.wuan.common.mq.processor;//package com.tellhow.mq.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pro.wuan.common.core.utils.SpringUtil;
import pro.wuan.common.mq.constant.TagEnum;
import pro.wuan.common.mq.handler.IMessageHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Component
@Slf4j
public class MQConsumeMsgListenerProcessor implements MessageListenerConcurrently {

    @Value("${tellhow.rocketmq.consumer.topic:default}")
    private String topic;
    @Value("${tellhow.rocketmq.consumer.reconsumeTimes}")
    private Integer reconsumeTimes;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            for (MessageExt msg : msgs) {
                log.info("接受到的消息为：" + msg.toString());
                //匹配topic和tags
                if (topic.contains(msg.getTopic() + ";") && Objects.nonNull(TagEnum.getTagEnum(msg.getTags()))) {
                    log.info("接收的body体：{}", new String(msg.getBody(), StandardCharsets.UTF_8));
                    this.handleMessage(msg);
                }
            }
        } catch (Exception ex) {
            log.error("consumeMessage error:", ex);
            if (msgs.getFirst().getReconsumeTimes() == reconsumeTimes) {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;// 成功
            } else {
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;// 重试
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    private void handleMessage(MessageExt msg) {
        Map<String, IMessageHandler> map = SpringUtil.getBeansOfType(IMessageHandler.class);
        for (Map.Entry<String, IMessageHandler> entry : map.entrySet()) {
            entry.getValue().handlerMessage(msg);
        }
    }
}

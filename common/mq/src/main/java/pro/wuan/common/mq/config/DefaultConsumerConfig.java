package pro.wuan.common.mq.config;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import pro.wuan.common.mq.constant.GroupEnum;
import pro.wuan.common.mq.exception.RocketMQException;
import pro.wuan.common.mq.model.GroupTopicTag;

import java.util.List;

/**
 * 默认消费者,用于给具体消费者继承
 *
 * @author: oldone
 * @date: 2021/9/15 16:56
 */
@Slf4j
@Configuration
public abstract class DefaultConsumerConfig {

    @Autowired
    private MQConsumerConfig mqConsumerConfig;

    /**
     * 消息模式，广播、集群
     *
     * @return
     */
    public MessageModel getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    protected MessageModel messageModel;

    /**
     * 批量监听
     *
     * @param groupTopicTags
     */
    public void batchListener(List<GroupTopicTag> groupTopicTags) throws MQClientException {
        if (CollectionUtils.isEmpty(groupTopicTags)) {
            return;
        }

        try {
            for (GroupTopicTag groupTopicTag : groupTopicTags) {
                this.listener(groupTopicTag);
            }
        } catch (MQClientException e) {
            throw e;
        }
    }

    // 开启消费者监听服务
    public void listener(GroupEnum groupEnum) throws MQClientException {
        if (groupEnum == null) {
            throw new RocketMQException("groupEnum is null!");
        }
        GroupTopicTag groupTopicTag = new GroupTopicTag();
        groupTopicTag.setGroup(groupEnum.getGroup());
        groupTopicTag.setTopic(groupEnum.getTopic());
        groupTopicTag.setTag(groupEnum.getTag());
        this.listener(groupTopicTag);
    }

    public void listener(GroupTopicTag groupTopicTag) throws MQClientException {
        if (groupTopicTag == null) {
            throw new RocketMQException("groupTopicTag is null!");
        }
        if (StringUtils.isEmpty(mqConsumerConfig.getNamesrvAddr())) {
            throw new RocketMQException("namesrvAddr is null!");
        }

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupTopicTag.getGroup());
        //设置广播模式
        if (messageModel != null) {
            consumer.setMessageModel(messageModel);
        } else {
            consumer.setMessageModel(mqConsumerConfig.getMessageModel());
        }
        consumer.setNamesrvAddr(mqConsumerConfig.getNamesrvAddr());
        consumer.setConsumeThreadMin(mqConsumerConfig.getConsumeThreadMin());
        consumer.setConsumeThreadMax(mqConsumerConfig.getConsumeThreadMax());
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                return DefaultConsumerConfig.this.dealBody(groupTopicTag.getTopic(), msgs);
            }
        });
        /**
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        /**
         * 设置一次消费消息的条数，默认为1条
         */
        consumer.setConsumeMessageBatchMaxSize(mqConsumerConfig.getConsumeMessageBatchMaxSize());
        try {
            if (StringUtils.isNotEmpty(groupTopicTag.getTopic())) {
                String[] topics = groupTopicTag.getTopic().split(";");
                for (int i = 0; i < topics.length; i++) {
                    consumer.subscribe(topics[i], groupTopicTag.getTag());
                }
            }
            consumer.start();
            log.info("consumer is start !!! groupName:{},topic:{},tag:{},namesrvAddr:{}", groupTopicTag.getGroup(), groupTopicTag.getTopic(), groupTopicTag.getTag(), mqConsumerConfig.getNamesrvAddr());
        } catch (MQClientException e) {
            log.error("consumer is start !!! groupName:{},topic:{},tag:{},namesrvAddr:{}", groupTopicTag.getGroup(), groupTopicTag.getTopic(), groupTopicTag.getTag(), mqConsumerConfig.getNamesrvAddr(),
                    e);

            throw e;
        }
    }

    // 处理body的业务
    public ConsumeConcurrentlyStatus dealBody(String topic, List<MessageExt> msgs) {
        try {
            for (MessageExt msg : msgs) {
                log.info("接受到的消息为：" + msg.toString());
                //匹配topic和tags
                if (topic.contains(msg.getTopic())) {
                    log.info("接收的body体：" + new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET));
                    this.handleMessage(msg);
                }
            }
        } catch (Exception ex) {
            log.error("consumeMessage error:", ex);
            if (msgs.get(0).getReconsumeTimes() == mqConsumerConfig.getReconsumeTimes()) {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;// 成功
            } else {
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;// 重试
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 消息业务逻辑处理
     *
     * @param msgs
     */
    public abstract void handleMessage(Message msgs);
}

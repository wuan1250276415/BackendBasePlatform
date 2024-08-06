package pro.wuan.common.mq.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.mq.constant.MsgStateEnum;
import pro.wuan.common.mq.model.MqMessage;
import pro.wuan.common.mq.service.MqMessageService;
import pro.wuan.common.mq.service.MqProducerService;
import pro.wuan.common.mq.task.MessageTask;
import pro.wuan.common.mq.task.MessageTaskManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description: 消息生产者实现类
 * @author: oldone
 * @date: 2020/9/9 10:31
 */
@Slf4j
@Component(value = "mqProducerService")
public class MqProducerServiceImpl implements MqProducerService {
    @Value("${rocketmq.send.timeout:3000}")
    private long timeout;
    @Value("${rocketmq.send.maxRetryNum:3}")
    private Integer maxRetryNum;
    @Value("${rocketmq.send.delayTime:10}")
    private Long delayTime;
    @Value("${tellhow.rocketmq.producer.topic:default}")
    private String topic;
    @Autowired
    private DefaultMQProducer producer;
    @Autowired
    private MqMessageService mqMessageService;


    /**
     * 同步发送消息
     *
     * @param message mq消息体
     * @throws Exception
     */
    public Result sendMsg(MqMessage message) {
        try {
            Result msgResult = this.buildMessage(message);
            if (!msgResult.isSuccess()) {
                return msgResult;
            }
            if (producer == null) {
                return Result.failure("未启用消息发送");
            }
            SendResult sendResult = producer.send((Message) msgResult.getResult(), timeout);
            log.info("MQ结果返回：" + JSON.toJSONString(sendResult));
            //判断发送是否成功
            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                //发送成功则删除待发送mq消息
                mqMessageService.deleteById(message.getId());
                return Result.success("发送消息成功");
            } else {
                return Result.failure("发送消息失败");
            }
        } catch (Exception e) {
            log.error("sendMsg error:" + e);
            return Result.failure("消息发送失败");
        }
    }

    /**
     * 异步发送消息并回调
     *
     * @param message mq消息
     * @throws Exception
     */
    public Result asynSendMsg(MqMessage message) {
        try {
            //发送次数+1
            message.setRetryNum((message.getRetryNum() == null ? 0 : message.getRetryNum()) + 1);
            Result msgResult = this.buildMessage(message);
            if (!msgResult.isSuccess()) {
                return msgResult;
            }
            producer.send((Message) msgResult.getResult(), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    //判断发送是否成功
                    if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                        //发送成功则删除待发送mq消息
                        mqMessageService.deleteById(message.getId());
                    } else {
                        message.setStatus(MsgStateEnum.FAILURE.getState());
                        //未超过最大发送次数
                        if (message.getRetryNum() < maxRetryNum) {
                            //发送失败的放到定时任务线程池重新发送
                            MessageTaskManager.getExecutorService().schedule(new MessageTask(message), message.getRetryNum() * delayTime, TimeUnit.SECONDS);
                        }
                        mqMessageService.updateById(message);
                    }
                }

                @Override
                public void onException(Throwable e) {
                    log.error("消息回调异常:" + ExceptionUtils.getMessage(e));
                    message.setStatus(MsgStateEnum.FAILURE.getState());
                    mqMessageService.updateById(message);
                }
            }, timeout);
            return Result.success("mq发送消息成功");
        } catch (Exception e) {
            log.error("asynSendMsg error:" + e);
            return Result.failure("消息发送失败");
        }
    }

    /**
     * 异步发送消息且不回调
     *
     * @param message
     * @return
     */
    public Result sendOneWay(MqMessage message) {
        try {
            Result msgResult = this.buildMessage(message);
            if (!msgResult.isSuccess()) {
                return msgResult;
            }
            producer.sendOneway((Message) msgResult.getResult());
            return Result.success("mq发送消息成功");
        } catch (Exception e) {
            log.error("asynSendMsg error:" + e);
            return Result.failure("消息发送失败");
        }
    }

    /**
     * 构建待发送消息
     *
     * @param message
     * @return
     */
    private Result buildMessage(MqMessage message) {
        try {
            if (message == null) {
                return Result.failure("消息体不能为空");
            }
            if (message.getGroupEnum() == null && Strings.isEmpty(message.getGroup())) {
                return Result.failure("消息分组不能为空");
            }
            if (message.getTagEnum() == null && Strings.isEmpty(message.getTag())) {
                return Result.failure("业务类型不能为空");
            }
            //保存待发送消息
            if (mqMessageService.insert(message) < 1) {
                //保存失败
                return Result.failure("消息入库失败");
            }
            String group = message.getGroupEnum() != null ? message.getGroupEnum().getGroup() : message.getGroup();
            String msgTopic = Strings.isNotEmpty(message.getTopic()) ? message.getTopic() : topic;
            String tag = message.getGroupEnum() != null ? message.getGroupEnum().getTag() : message.getTag();
            producer.setProducerGroup(group);
            Message msg;
            msg = new Message(msgTopic,
                    tag, message.getKey(),
                    JSON.toJSONString(message).getBytes(RemotingHelper.DEFAULT_CHARSET)
            );
            msg.putUserProperty("request-id", UUID.randomUUID().toString().replace("-", ""));
            log.info("MQ请求参数：" + JSON.toJSONString(message));
            return Result.success(msg);
        } catch (Exception e) {
            log.error("buildMessage error:" + e);
            return Result.failure("构建消息失败");
        }

    }

}

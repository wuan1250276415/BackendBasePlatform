package pro.wuan.common.mq.service;


import pro.wuan.common.core.model.Result;
import pro.wuan.common.mq.model.MqMessage;

/**
 * @description: 消息生产者接口
 * @author: oldone
 * @date: 2020/9/9 10:17
 */
public interface MqProducerService {

    /**
     * 发送消息
     *
     * @param message mq消息
     * @throws Exception
     */
    Result sendMsg(MqMessage message);

    /**
     * 异步发送消息并回调
     *
     * @param message      mq消息
     * @throws Exception
     */
    Result asynSendMsg(MqMessage message);

    /**
     * 异步发送消息且不回调
     *
     * @param message
     * @return
     */
    Result sendOneWay(MqMessage message);
}

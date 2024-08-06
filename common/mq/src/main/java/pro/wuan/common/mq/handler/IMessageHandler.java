package pro.wuan.common.mq.handler;

import org.apache.rocketmq.common.message.Message;

/**
 * @description:
 * @author: oldone
 * @date: 2020/9/9 14:32
 */
public interface IMessageHandler {
    /**
     * 消息处理
     * @param message
     */
    void handlerMessage(Message message);
}

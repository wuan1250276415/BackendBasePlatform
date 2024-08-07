package pro.wuan.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;
import pro.wuan.common.mq.handler.IMessageHandler;

/**
 * @program: tellhowcloud
 * @description: MQ消费
 * @author: HawkWang
 * @create: 2021-08-27 14:47
 **/
@Slf4j
@Service
public class MessageHandler implements IMessageHandler {
    @Override
    public void handlerMessage(Message message) {
        //什么都不做
    }
}

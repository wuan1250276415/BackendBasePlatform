package pro.wuan.common.mq.task;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.mq.model.MqMessage;
import pro.wuan.common.mq.service.MqProducerService;

import java.util.concurrent.Callable;

/**
 * 消息任务
 *
 * @author: oldone
 * @date: 2021/9/18 14:01
 */
@Slf4j
public class MessageTask implements Callable<Result> {

    /**
     * 待发送消息体
     */
    private MqMessage mqMessage;


    public MessageTask(MqMessage mqMessage) {
        this.mqMessage = mqMessage;

    }

    /**
     * 消息生产者
     */
    @Resource
    private MqProducerService mqProducerService;

    @Override
    public Result call() {
        log.info("message info:" + JSON.toJSONString(mqMessage));
        if (mqMessage == null) {
            return Result.failure("消息体不能为空");
        }
        /**
         * 异步发送消息
         */
        Result result = mqProducerService.asynSendMsg(mqMessage);
        log.info("send result:" + JSON.toJSONString(result));
        return result;
    }
}

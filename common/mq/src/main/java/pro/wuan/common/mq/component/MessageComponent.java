package pro.wuan.common.mq.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.wuan.common.mq.service.MqMessageService;

/**
 * 消息组件--用于项目启动执行任务
 *
 * @author: oldone
 * @date: 2021/9/19 17:17
 */
@Slf4j
@Component
public class MessageComponent {

    @Autowired
    private MqMessageService mqMessageService;

//    @Scheduled(cron = "")
//    public void initMessageTask() {
//        //获取发送失败的
//        List<MqMessage> messages = mqMessageService.getListByState(MsgStateEnum.FAILURE.getState());
//        if (CollectionUtils.isEmpty(messages)) {
//            return;
//        }
//        List<MessageTask> tasks = messages.stream().map(y -> new MessageTask(y)).collect(Collectors.toList());
//        try {
//            MessageTaskManager.getExecutorService().invokeAll(tasks);
//        } catch (InterruptedException e) {
//            log.error("initMessageTask InterruptedException error:", e);
//        }
//    }
}

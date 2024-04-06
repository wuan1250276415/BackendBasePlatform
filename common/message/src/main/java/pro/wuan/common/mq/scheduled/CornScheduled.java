package pro.wuan.common.mq.scheduled;

import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.wuan.common.mq.message.MessageRepository;
import pro.wuan.feignapi.messageapi.entity.Message;

import java.util.List;

@Component
public class CornScheduled {

    private final RabbitTemplate template;
    private final MessageRepository messageRepository;

    public CornScheduled(RabbitTemplate template, MessageRepository messageRepository) {
        this.template = template;
        this.messageRepository = messageRepository;
    }

    @Scheduled(fixedDelay = 10000) // 每10秒执行一次
    @Transactional
    public void publishMessages() {
        // 查询状态为“待处理”的消息
        List<Message> messages = messageRepository.findMessagesByStatus("1");

        for (Message message : messages) {
            // 发布消息到消息队列
            publishToQueue(message);

            // 更新消息状态为“已发送”
            messageRepository.updateMessageStatus(message.getId(), "2");
        }
    }

    private void publishToQueue(Message message) {
        // 发布消息到消息队列
        template.convertAndSend(message.getQueue(), message.getContent());
    }
}

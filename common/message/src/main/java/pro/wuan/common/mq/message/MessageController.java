package pro.wuan.common.mq.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.wuan.feignapi.messageapi.entity.Message;

@RequestMapping("/message")
@RestController
public class MessageController{

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final RabbitTemplate rabbitTemplate;

    private final MessageRepository messageRepository;

    public MessageController(RabbitTemplate rabbitTemplate, MessageRepository messageRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageRepository = messageRepository;
    }

    /**
     * 创建消息
     * @param message 消息
     * @return 消息
     */
    @PostMapping("/create")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        messageRepository.save(message);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/send/{queue}/{msg}")
    public ResponseEntity<String> sendMsg(@PathVariable String queue,@PathVariable String msg){
        log.info("Sending message to queue: {}", queue);
        rabbitTemplate.convertAndSend(queue, msg);
        return ResponseEntity.ok("Message sent to "+queue+": " + msg);
    }
}

package pro.wuan.common.mq.message;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.feignapi.messageapi.entity.Message;

@RequestMapping("/message")
@RestController
public class MessageController{

    private final MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
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
}

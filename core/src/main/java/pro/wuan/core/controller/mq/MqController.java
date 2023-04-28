package pro.wuan.core.controller.mq;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.mq.config.MessageProducer;

@RestController
public class MqController {

    private final MessageProducer messageProducer;

    public MqController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @GetMapping("/send")
    public String sendMessage(@RequestParam String message) {
        messageProducer.sendMessage("myDirectExchange", "myRoutingKey", message);
        return "Message sent: " + message;
    }
}

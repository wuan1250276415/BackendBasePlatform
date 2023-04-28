package pro.wuan.common.mq.config;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @RabbitListener(queues = "myQueue")
    public void handleMessage(String message) {
        System.out.println("Received message: " + message);
    }
}

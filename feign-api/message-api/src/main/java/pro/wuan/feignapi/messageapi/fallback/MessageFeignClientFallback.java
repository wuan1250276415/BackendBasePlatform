package pro.wuan.feignapi.messageapi.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pro.wuan.feignapi.messageapi.entity.Message;
import pro.wuan.feignapi.messageapi.feign.MessageFeignClient;

@Component
@Slf4j
public class MessageFeignClientFallback implements MessageFeignClient {

    @Override
    public ResponseEntity<Message> createMessage(Message message) {
        log.info("createMessage fallback");
        return null;
    }

    @Override
    public ResponseEntity<String> sendMsg(String queue, String msg) {
        log.info("sendMsg fallback");
        return null;
    }
}

package pro.wuan.feignapi.messageapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pro.wuan.feignapi.messageapi.entity.Message;
import pro.wuan.feignapi.messageapi.fallback.MessageFeignClientFallback;

@FeignClient(value = "message",fallback = MessageFeignClientFallback.class)
public interface MessageFeignClient {

    @PostMapping("/message/create")
    ResponseEntity<Message> createMessage(@RequestBody Message message);

    @GetMapping("/message/send/{queue}/{msg}")
    ResponseEntity<String> sendMsg(@PathVariable String queue, @PathVariable String msg);
}

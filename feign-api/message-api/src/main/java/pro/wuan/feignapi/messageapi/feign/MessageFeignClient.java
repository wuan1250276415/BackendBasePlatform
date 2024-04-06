package pro.wuan.feignapi.messageapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pro.wuan.feignapi.messageapi.entity.Message;
import pro.wuan.feignapi.messageapi.fallback.MessageFeignClientFallback;

@FeignClient(value = "message",path = "/feign/message",fallback = MessageFeignClientFallback.class)
public interface MessageFeignClient {

    @PostMapping("/message/create")
    void createMessage(@RequestBody Message message);
}

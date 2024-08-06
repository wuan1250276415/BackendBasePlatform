package pro.wuan.feignapi.userapi.fallback;

import org.springframework.stereotype.Component;
import pro.wuan.feignapi.userapi.feign.HelloFeignClient;

/**
 * @description:
 * @author: oldone
 * @date: 2021/8/19 16:32
 */
@Component
public class HelloFeignClientFallback implements HelloFeignClient {

    @Override
    public String say(String name) {
        return "Not Bad";
    }
}

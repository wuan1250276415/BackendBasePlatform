package pro.wuan.feignapi.userapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.constant.AppConstant;
import pro.wuan.feignapi.userapi.fallback.HelloFeignClientFallback;

/**
 * @description:
 * @author: oldone
 * @date: 2021/8/19 16:27
 */
@FeignClient(
        value = AppConstant.APPLICATION_SYS_NAME,
        fallback = HelloFeignClientFallback.class
)
public interface HelloFeignClient {

    @GetMapping("/hello/say")
    String say(@RequestParam(value = "name") String name);
}

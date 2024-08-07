package fallback;

import feign.BusFeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.model.Result;

/**
 * @program: tellhowcloud
 * @description: 系统注册应用业务的降级返回默认值
 **/
@Component
public class BusFeignClientFallback implements BusFeignClient {


    @Override
    public Result setRead(@RequestParam("key") String key) {
        return null;
    }

    @Override
    public Result getAllUser() {
        return null;
    }

    @Override
    public Result<String> sendMessageToUser(@RequestParam("userId") String userId, @RequestParam("message") String message) {
        return null;
    }

    @Override
    public Result getAllAppDataEx() {
        return Result.failure("feign获取失败！");
    }
    @Override
    public Result sendMessageToAppDataEx(@RequestParam("userId") String userId,@RequestParam("message") String message) {
        return Result.failure("feign获取失败！");
    }
    @Override
    public Result getAllFrontAppDataEx() {
        return Result.failure("feign获取失败！");
    }
    @Override
    public Result sendWsMessageToFrontAppDataEx(@RequestParam("userId") String userId,@RequestParam("message") String message) {
        return Result.failure("feign获取失败！");
    }
}

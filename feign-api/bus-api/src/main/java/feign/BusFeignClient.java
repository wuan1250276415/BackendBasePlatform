package feign;

import fallback.BusFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.wuan.common.core.model.Result;

import java.util.List;

@FeignClient(
        value = "tellhow-components-bus",
        fallback = BusFeignClientFallback.class,
        path = "/feign/bus"
)
public interface BusFeignClient {
    @GetMapping("/setRead")
    @ResponseBody
    Result setRead(@RequestParam("key") String key);

    /**
     * 获取所有在线用户
     * @return
     */
    @GetMapping("/get/all/user")
    @ResponseBody
    Result<List<String>> getAllUser();

    /**
     * 发送用户消息
     * @return
     */
    @GetMapping("/send/message/to/user")
    @ResponseBody
    Result<String> sendMessageToUser(@RequestParam("userId") String userId,@RequestParam("message") String message);

    /**
     * 后端-获取所有在线AppDataEx
     * @return
     */
    @GetMapping(value = "/get/all/appDataEx",produces = { "application/json;charset=UTF-8"})
    @ResponseBody
    Result<List<String>> getAllAppDataEx();


    /**
     * 后端-发送AppDataEx消息
     * @return
     */
    @GetMapping(value = "/send/message/to/appDataEx",produces = { "application/json;charset=UTF-8"})
    @ResponseBody
    Result<String> sendMessageToAppDataEx(@RequestParam("userId") String userId,@RequestParam("message") String message);

    /**
     * 前端-获取所有前端在线AppDataEx
     * @return
     */
    @GetMapping(value = "/get/allFront/appDataEx",produces = { "application/json;charset=UTF-8"})
    @ResponseBody
    Result<List<String>> getAllFrontAppDataEx();


    /**
     * 前端-发送AppDataEx消息
     * @return
     */
    @GetMapping(value = "/send/WsMessage/toFront/appDataEx",produces = { "application/json;charset=UTF-8"})
    @ResponseBody
    Result<String> sendWsMessageToFrontAppDataEx(@RequestParam("userId") String userId,@RequestParam("message") String message);

}

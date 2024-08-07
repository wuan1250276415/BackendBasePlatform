package feign;

import fallback.ParamFeignClientFallback;
import model.SysParamModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.constant.AppConstant;

import java.util.List;
import java.util.Map;

/**
 * 数据字典feign接口
 *
 * @author: oldone
 * @date: 2021/9/8 10:53
 */
@FeignClient(
        value = AppConstant.APPLICATION_SYS_NAME,
        fallback = ParamFeignClientFallback.class,
        path = "/feign/param"

)
public interface ParamFeignClient {
    /**
     * 向sys注册系统参数
     *
     * @param sysParamModels      获取参数对象
     * @param sysParamModels 系统参数对象列表
     */
    @PostMapping(value = "/registParam", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Boolean registParam(@RequestBody List<SysParamModel> sysParamModels);

    /**
     * 根据应用的paramClass全类名码获取这个类的所有属性参数
     *
     * @param paramClass 注册的参数类
     * @return 属性与值的map
     */
    @GetMapping("/selectByParamClass")
    Map<String, Object> getSysParam(@RequestParam("paramClass") String paramClass);
}

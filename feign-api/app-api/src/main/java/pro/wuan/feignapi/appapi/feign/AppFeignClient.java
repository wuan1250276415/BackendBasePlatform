package pro.wuan.feignapi.appapi.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.constant.AppConstant;
import pro.wuan.common.core.service.IBaseFeignQuerySerivce;
import pro.wuan.feignapi.appapi.OrgAppModel;
import pro.wuan.feignapi.appapi.fallback.AppFeignClientFallback;

import java.util.List;


@FeignClient(
        value = AppConstant.APPLICATION_SYS_NAME,
        fallback = AppFeignClientFallback.class,
        path = "/feign/org/app"
)
public interface AppFeignClient extends IBaseFeignQuerySerivce<OrgAppModel> {

    /**
     * 根据应用id获取应用
     *
     * @param id 应用id
     * @return 应用
     */
    @GetMapping("/getAppById")
    OrgAppModel getAppById(@RequestParam("id") Long id);

    /**
     * 根据应用编码获取应用
     *
     * @param code 应用编码
     * @return 应用
     */
    @GetMapping("/getAppByCode")
    OrgAppModel getAppByCode(@RequestParam("code") String code);

    /**
     * 根据应用id列表获取应用列表
     *
     * @param idList 应用id列表
     * @return 应用列表
     */
    @PostMapping("/getAppListByIdList")
    List<OrgAppModel> getAppListByIdList(@RequestBody List<Long> idList);

}

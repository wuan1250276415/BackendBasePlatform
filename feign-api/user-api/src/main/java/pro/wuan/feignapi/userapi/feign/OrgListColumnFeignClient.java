package pro.wuan.feignapi.userapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.constant.AppConstant;
import pro.wuan.feignapi.userapi.entity.OrgListColumnModel;
import pro.wuan.feignapi.userapi.fallback.OrgListColumnFeignClientFaillback;

import java.util.List;

/**
 * 数据权限的Feign接口
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-12-30 19:39
 **/
@FeignClient(
        value = AppConstant.APPLICATION_SYS_NAME,
        fallback = OrgListColumnFeignClientFaillback.class)
public interface OrgListColumnFeignClient {

    @GetMapping(value = "/feign/org/listColumn/selectByUrl")
    public List<OrgListColumnModel> findListColumnByUrl(@RequestParam("url") String url);
}

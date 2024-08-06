package pro.wuan.feignapi.userapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import pro.wuan.common.core.constant.AppConstant;
import pro.wuan.common.core.service.IBaseFeignQuerySerivce;
import pro.wuan.feignapi.userapi.entity.OrgBusinessResourceModel;
import pro.wuan.feignapi.userapi.fallback.OrgBusinessResourceFeignClientFallback;
import pro.wuan.feignapi.userapi.vo.SearchMenuVO;

import java.util.List;

/**
 * 业务资源
 *
 * @author: liumin
 * @date: 2021/11/19 10:05
 */
@FeignClient(
        value = AppConstant.APPLICATION_SYS_NAME,
        fallback = OrgBusinessResourceFeignClientFallback.class,
        path = "/feign/org/businessResource"
)
public interface OrgBusinessResourceFeignClient extends IBaseFeignQuerySerivce<OrgBusinessResourceModel> {

    /**
     * 获取所有搜索菜单列表
     *
     * @return 搜索菜单列表
     */
    @GetMapping("/getAllSearchMenuList")
    List<SearchMenuVO> getAllSearchMenuList();

}

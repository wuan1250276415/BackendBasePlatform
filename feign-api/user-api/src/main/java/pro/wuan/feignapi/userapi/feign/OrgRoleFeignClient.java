package pro.wuan.feignapi.userapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.constant.AppConstant;
import pro.wuan.common.core.service.IBaseFeignQuerySerivce;
import pro.wuan.feignapi.userapi.entity.OrgRoleModel;
import pro.wuan.feignapi.userapi.fallback.OrgPostFeignClientFallback;

import java.util.List;

/**
 * 角色
 *
 * @author: liumin
 * @date: 2021/11/19 10:05
 */
@FeignClient(
        value = AppConstant.APPLICATION_SYS_NAME,
        fallback = OrgPostFeignClientFallback.class,
        path = "/feign/org/role"
)
public interface OrgRoleFeignClient extends IBaseFeignQuerySerivce<OrgRoleModel> {

    /**
     * 根据组织机构id获取关联的角色列表
     *
     * @param organId 组织机构id
     * @return 角色列表
     */
    @GetMapping("/getRoleListByOrganId")
    List<OrgRoleModel> getRoleListByOrganId(@RequestParam("organId") Long organId);

}

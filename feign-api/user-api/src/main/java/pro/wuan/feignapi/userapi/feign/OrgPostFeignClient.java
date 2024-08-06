package pro.wuan.feignapi.userapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.constant.AppConstant;
import pro.wuan.common.core.service.IBaseFeignQuerySerivce;
import pro.wuan.feignapi.userapi.entity.OrgPostModel;
import pro.wuan.feignapi.userapi.fallback.OrgPostFeignClientFallback;

import java.util.List;

/**
 * 岗位
 *
 * @author: liumin
 * @date: 2021/11/19 10:05
 */
@FeignClient(
        value = AppConstant.APPLICATION_SYS_NAME,
        fallback = OrgPostFeignClientFallback.class,
        path = "/feign/org/post"
)
public interface OrgPostFeignClient extends IBaseFeignQuerySerivce<OrgPostModel> {

    /**
     * 根据用户id获取岗位列表
     *
     * @param userId 用户id
     * @return 组织机构列表
     */
    @GetMapping("/getPostListByUserId")
    List<OrgPostModel> getPostListByUserId(@RequestParam("userId") Long userId);


    /**
     * 根据组织id获取岗位列表
     *
     * @param organId 组织id
     * @return 岗位列表
     */
    @GetMapping("/getPostListByOrganId")
    List<OrgPostModel> getPostListByOrganId(@RequestParam("organId") Long organId);

}

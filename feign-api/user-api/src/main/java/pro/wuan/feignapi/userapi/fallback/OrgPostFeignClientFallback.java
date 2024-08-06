package pro.wuan.feignapi.userapi.fallback;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.feignapi.userapi.entity.OrgPostModel;
import pro.wuan.feignapi.userapi.feign.OrgPostFeignClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 岗位
 *
 * @author: liumin
 * @date: 2021/11/19 10:07
 */
@Component
public class OrgPostFeignClientFallback implements OrgPostFeignClient {

    @Override
    public OrgPostModel selectById(Long id) {
        return null;
    }

    @Override
    public List<OrgPostModel> selectBatchByIds(List<Long> ids) {
        return new ArrayList<>();
    }

    /**
     * 根据用户id获取岗位列表
     *
     * @param userId 用户id
     * @return 组织机构列表
     */
    @Override
    public List<OrgPostModel> getPostListByUserId(@RequestParam("userId") Long userId) {
        return new ArrayList<>();
    }

    /**
     * 根据组织id获取岗位列表
     *
     * @param organId 组织id
     * @return 岗位列表
     */
    @Override
    public List<OrgPostModel> getPostListByOrganId(@RequestParam("organId") Long organId) {
        return new ArrayList<>();
    }

}

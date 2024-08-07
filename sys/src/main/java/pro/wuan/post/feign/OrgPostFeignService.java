package pro.wuan.post.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.web.feign.service.impl.QueryFeignService;
import pro.wuan.feignapi.userapi.entity.OrgPostModel;
import pro.wuan.post.mapper.OrgPostMapper;
import pro.wuan.post.service.IOrgPostService;

import java.util.List;

/**
 * 岗位
 *
 * @author: liumin
 * @date: 2021-08-30 11:19:43
 */
@Slf4j
@RestController
@RequestMapping("/feign/org/post")
public class OrgPostFeignService extends QueryFeignService<IOrgPostService, OrgPostMapper, OrgPostModel> {

    @Autowired
    private IOrgPostService postService;

    /**
     * 根据用户id获取岗位列表
     *
     * @param userId 用户id
     * @return 组织机构列表
     */
    @GetMapping("/getPostListByUserId")
    public List<OrgPostModel> getPostListByUserId(@RequestParam("userId") Long userId) {
        try {
            return postService.getPostListByUserId(userId);
        } catch (Exception e) {
            log.error("post getPostListByUserId error:" + e);
            return null;
        }
    }
    /**
     * 根据组织id获取岗位列表
     *
     * @param organId 组织id
     * @return 岗位列表
     */
    @GetMapping("/getPostListByOrganId")
    List<OrgPostModel> getPostListByOrganId(@RequestParam("organId") Long organId) {
        try {
            return postService.getPostListByOrganId(organId);
        } catch (Exception e) {
            log.error("post getPostListByOrganId error:" + e);
            return null;
        }
    }

}

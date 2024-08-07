package pro.wuan.user.service;


import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.OrgUserPostModel;
import pro.wuan.user.mapper.OrgUserPostMapper;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户岗位关联表
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
public interface IOrgUserPostService extends IBaseService<OrgUserPostMapper, OrgUserPostModel> {

    /**
     * 批量保存用户岗位关联表
     *
     * @param userId
     * @param postIdList
     * @return
     */
    public void batchSaveUserPost(@NotNull(message = "userId不能为空") Long userId, List<Long> postIdList);

    /**
     * 批量删除用户岗位关联表
     *
     * @param userId
     * @param postIdList
     * @return
     */
    public int batchDeleteUserPost(@NotNull(message = "userId不能为空") Long userId, List<Long> postIdList);

    /**
     * 批量删除用户岗位关联表
     *
     * @param userId
     * @return
     */
    public int batchDeleteUserPost(@NotNull(message = "userId不能为空") Long userId);

    /**
     * 根据岗位id列表查询用户岗位关联实体信息
     *
     * @param postIdList
     * @return
     */
    public List<OrgUserPostModel> getUserPostListByPostIdList(List<Long> postIdList);

}
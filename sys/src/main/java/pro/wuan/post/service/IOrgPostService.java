package pro.wuan.post.service;


import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.OrgPostModel;
import pro.wuan.post.mapper.OrgPostMapper;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 岗位
 *
 * @author: liumin
 * @date: 2021-08-30 11:19:43
 */
public interface IOrgPostService extends IBaseService<OrgPostMapper, OrgPostModel> {

    /**
     * 保存岗位
     *
     * @param postModel 岗位实体
     * @return 岗位实体
     */
    public Result<OrgPostModel> savePost(OrgPostModel postModel);

    /**
     * 更新岗位
     *
     * @param postModel 岗位实体
     * @return 岗位实体
     */
    public Result<OrgPostModel> updatePost(OrgPostModel postModel);

    /**
     * 删除岗位
     *
     * @param id 岗位id
     * @return 删除成功/失败
     */
    public Result<String> deletePost(@NotNull(message = "id不能为空") Long id);

    /**
     * 获取组织机构下的岗位列表
     *
     * @param organId 组织机构id
     * @return 岗位实体列表
     */
    public Result<List<OrgPostModel>> getPostListByOrgan(@NotNull(message = "organId不能为空") Long organId);

    /**
     * 获取组织机构下的岗位列表
     *
     * @param organId 组织机构id
     * @return 岗位实体列表
     */
    public List<OrgPostModel> getPostListByOrganId(@NotNull(message = "organId不能为空") Long organId);

    /**
     * 根据用户id获取关联的岗位列表
     *
     * @param userId 用户id
     * @return 岗位列表
     */
    public List<OrgPostModel> getPostListByUserId(@NotNull(message = "userId不能为空") Long userId);

}
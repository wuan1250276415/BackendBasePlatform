package pro.wuan.user.service;

import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.OrgUserRoleModel;
import pro.wuan.user.mapper.OrgUserRoleMapper;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户角色关联表
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
public interface IOrgUserRoleService extends IBaseService<OrgUserRoleMapper, OrgUserRoleModel> {

    /**
     * 批量保存用户角色关联表
     *
     * @param userId
     * @param roleIdList
     * @return
     */
    public void batchSaveUserRole(@NotNull(message = "userId不能为空") Long userId, List<Long> roleIdList);

    /**
     * 批量删除用户角色关联表
     *
     * @param userId
     * @param roleIdList
     * @return
     */
    public int batchDeleteUserRole(@NotNull(message = "userId不能为空") Long userId, List<Long> roleIdList);

    /**
     * 批量删除用户角色关联表
     *
     * @param userId
     * @return
     */
    public int batchDeleteUserRole(@NotNull(message = "userId不能为空") Long userId);

    /**
     * 批量保存角色用户关联表
     *
     * @param roleId
     * @param userIdList
     * @return
     */
    public Result batchSaveRoleUser(@NotNull(message = "roleId不能为空") Long roleId, List<Long> userIdList);

    /**
     * 批量删除角色用户关联表
     *
     * @param roleId
     * @return
     */
    public int batchDeleteRoleUser(@NotNull(message = "roleId不能为空") Long roleId);

    /**
     * 根据用户id查询用户角色关联实体信息
     *
     * @param userId
     * @return
     */
    public List<OrgUserRoleModel> getUserRoleListByUserId(@NotNull(message = "userId不能为空") Long userId);

    /**
     * 根据角色id查询用户角色关联实体信息
     *
     * @param roleId
     * @return
     */
    public List<OrgUserRoleModel> getUserRoleListByRoleId(@NotNull(message = "roleId不能为空") Long roleId);

    /**
     * 根据角色id列表查询用户角色关联实体信息
     *
     * @param roleIdList
     * @return
     */
    public List<OrgUserRoleModel> getUserRoleListByRoleIdList(List<Long> roleIdList);

    /**
     * 根据单位id集合和角色名称集合获取用户角色列表
     *
     * @param organIds     机构id集合
     * @param roleNameList 角色名称集合
     * @return
     */
    List<OrgUserRoleModel> getUserRoleList(@RequestParam List<Long> organIds, List<String> roleNameList);

    /**
     * 获取角色id集合
     *
     * @param organIds
     * @param roleNameList
     * @return
     */
    List<Long> getRoleIdList(List<Long> organIds, List<String> roleNameList);
}
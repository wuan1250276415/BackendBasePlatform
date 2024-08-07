package pro.wuan.privilege.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.OrgListColumnModel;
import pro.wuan.privilege.mapper.OrgListColumnMapper;
import pro.wuan.privilege.vo.BatchSaveListColumnVO;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 列表字段
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
public interface IOrgListColumnService extends IBaseService<OrgListColumnMapper, OrgListColumnModel> {

    /**
     * 批量新增字段实体
     *
     * @param batchSaveListColumnVO 批量新增字段实体VO
     * @return 新增成功/失败
     */
    public Result<String> batchSaveListColumn(BatchSaveListColumnVO batchSaveListColumnVO);

    /**
     * 字段名称是否存在
     *
     * @param id id
     * @param menuResourceId 菜单资源id
     * @param columnName 字段名称
     * @return true/false
     */
    public boolean isExistColumnName(Long id, Long menuResourceId, String columnName);

    /**
     * 根据应用id获取关联的列表字段列表
     *
     * @param appId 应用id
     * @return 列表字段列表
     */
    public List<OrgListColumnModel> getListColumnListByAppId(@NotNull(message = "appId不能为空") Long appId);

    /**
     * 根据用户id，应用id获取关联的列表字段列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 列表字段列表
     */
    public List<OrgListColumnModel> getListColumnListByUserAndApp(@NotNull(message = "userId不能为空") Long userId, @NotNull(message = "appId不能为空") Long appId);

    /**
     * 根据角色id列表获取关联的列表字段列表
     *
     * @param roleIdList 角色id列表
     * @return 列表字段列表
     */
    public List<OrgListColumnModel> getListColumnListByRoleIdList(@NotEmpty(message = "roleIdList不能为空") List<Long> roleIdList);

    /**
     * 批量删除菜单资源id关联的列表字段
     *
     * @param menuResourceId
     */
    public int batchDeleteListColumn(@NotNull(message = "menuResourceId不能为空") Long menuResourceId);

    /**
     * 根据菜单url获取当前用户的列表字段列表
     *
     * @param url 菜单url
     * @return 列表字段列表
     */
    @Deprecated
    public List<OrgListColumnModel> getListColumnListByCurrentUser(@NotBlank(message = "url不能为空") String url);

    /**
     * 根据菜单url获取当前用户的列表字段列表
     *
     * @param url 菜单url
     * @return 列表字段列表
     */
    List<OrgListColumnModel> getColumnListFilterByCurrentUser(@NotBlank(message = "url不能为空") String url);
}
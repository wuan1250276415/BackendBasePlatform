package pro.wuan.privilege.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.OrgBusinessResourceModel;
import pro.wuan.feignapi.userapi.vo.SearchMenuVO;
import pro.wuan.privilege.mapper.OrgBusinessResourceMapper;
import pro.wuan.privilege.vo.MenuTreeListQueryParamVO;
import pro.wuan.privilege.vo.ResourceTreeVO;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 业务资源
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
public interface IOrgBusinessResourceService extends IBaseService<OrgBusinessResourceMapper, OrgBusinessResourceModel> {

    /**
     * 查询菜单树形列表
     *
     * @param queryParamVO 菜单树形列表查询参数VO
     * @return 菜单树形列表
     */
    public List<OrgBusinessResourceModel> selectMenuTreeList(MenuTreeListQueryParamVO queryParamVO);

    /**
     * 根据应用id获取关联的菜单树形结构
     *
     * @param appId 应用id
     * @return 菜单树形结构
     */
    public List<OrgBusinessResourceModel> getMenuTreeByAppId(@NotNull(message = "appId不能为空") Long appId);

    /**
     * 根据菜单列表组装菜单树形结构
     *
     * @param menuList 菜单列表
     * @return 菜单树形结构
     */
    public List<OrgBusinessResourceModel> getMenuTree(List<OrgBusinessResourceModel> menuList);

    /**
     * 根据菜单树形结构获取菜单列表
     *
     * @param menuTree 菜单树形结构
     * @return 菜单列表
     */
    public List<OrgBusinessResourceModel> getMenuList(List<OrgBusinessResourceModel> menuTree);

    /**
     * 获取菜单树形结构（用于菜单编辑页面选择上级菜单）
     *
     * @param appId 应用id
     * @return 菜单树形结构
     */
    public ResourceTreeVO getMenuTree(@NotNull(message = "appId不能为空") Long appId);

    /**
     * 更新菜单/目录
     *
     * @param resourceModel 业务资源实体
     * @return 业务资源实体
     */
    public Result<OrgBusinessResourceModel> updateMenu(OrgBusinessResourceModel resourceModel);

    /**
     * 删除菜单/目录
     *
     * @param id 业务资源id
     * @return 删除成功/失败
     */
    public Result<String> deleteMenu(@NotNull(message = "id不能为空") Long id);

    /**
     * 编码是否存在
     *
     * @param id id
     * @param code 编码
     * @return true/false
     */
    public boolean isExistCode(Long id, @NotBlank(message = "code不能为空") String code);

    /**
     * 按钮编码是否存在
     *
     * @param id id
     * @param code 编码
     * @return true/false
     */
    boolean isExistCodeFunction(Long id, @NotBlank(message = "code不能为空") String code,Long menuResourceId);

    /**
     * 查询功能树形列表
     *
     * @param menuResourceId 菜单资源id
     * @return 功能树形列表
     */
    public List<OrgBusinessResourceModel> selectFunctionTreeList(@NotNull(message = "menuResourceId不能为空") Long menuResourceId);

    /**
     * 获取功能树形结构（按钮，用于菜单编辑页面选择上级菜单）
     *
     * @param menuResourceId 菜单资源id
     * @return 功能树形结构（按钮）
     */
    public ResourceTreeVO getFunctionTree(@NotNull(message = "menuResourceId不能为空") Long menuResourceId);

    /**
     * 删除功能（按钮）
     *
     * @param id 业务资源id
     * @return 删除成功/失败
     */
    public Result<String> deleteFunction(@NotNull(message = "id不能为空") Long id);

    /**
     * 根据用户id，应用id获取关联的菜单列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 菜单列表
     */
    public List<OrgBusinessResourceModel> getMenuListByUserAndApp(@NotNull(message = "userId不能为空") Long userId, @NotNull(message = "appId不能为空") Long appId);

    /**
     * 根据应用id获取关联的功能列表
     *
     * @param appId 应用id
     * @return 功能列表
     */
    public List<OrgBusinessResourceModel> getFunctionListByAppId(@NotNull(message = "appId不能为空") Long appId);

    /**
     * 根据用户id，应用id获取关联的功能列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 功能列表
     */
    public List<OrgBusinessResourceModel> getFunctionListByUserAndApp(@NotNull(message = "userId不能为空") Long userId, @NotNull(message = "appId不能为空") Long appId);

    /**
     * 根据角色id列表获取关联的业务资源列表
     *
     * @param roleIdList 角色id列表
     * @return 业务资源列表
     */
    public List<OrgBusinessResourceModel> getBusinessResourceListByRoleIdList(@NotEmpty(message = "roleIdList不能为空") List<Long> roleIdList);

    /**
     * 获取所有搜索菜单列表
     *
     * @return 搜索菜单列表
     */
    public List<SearchMenuVO> getAllSearchMenuList();

}
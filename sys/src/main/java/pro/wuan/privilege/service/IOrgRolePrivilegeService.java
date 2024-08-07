package pro.wuan.privilege.service;


import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.OrgRolePrivilegeModel;
import pro.wuan.privilege.mapper.OrgRolePrivilegeMapper;
import pro.wuan.privilege.vo.RolePrivilegeQueryVO;
import pro.wuan.privilege.vo.RolePrivilegeResultVO;
import pro.wuan.privilege.vo.RolePrivilegeSaveVO;

import javax.validation.constraints.NotNull;

/**
 * 角色权限关联表
 *
 * @author: liumin
 * @create 2021-08-30 11:15:00
 */
public interface IOrgRolePrivilegeService extends IBaseService<OrgRolePrivilegeMapper, OrgRolePrivilegeModel> {

    /**
     * 获取角色授权时的菜单树形结构
     *
     * @param roleId 角色id
     * @return 角色权限返回结果VO
     */
    public Result<RolePrivilegeResultVO> getMenuTreeByRole(@NotNull(message = "roleId不能为空") Long roleId);

    /**
     * 获取角色授权时的按钮树形结构
     *
     * @param queryVO 角色权限查询VO
     * @return 角色权限返回结果VO
     */
    public Result<RolePrivilegeResultVO> getButtonTreeByRole(RolePrivilegeQueryVO queryVO);

    /**
     * 获取角色授权时的列表树形结构
     *
     * @param queryVO 角色权限查询VO
     * @return 角色权限返回结果VO
     */
    public Result<RolePrivilegeResultVO> getListTreeByRole(RolePrivilegeQueryVO queryVO);

    /**
     * 获取角色授权时的数据方案树形结构
     *
     * @param queryVO 角色权限查询VO
     * @return 角色权限返回结果VO
     */
    public Result<RolePrivilegeResultVO> getDataSchemeTreeByRole(RolePrivilegeQueryVO queryVO);

    /**
     * 保存角色数据权限
     *
     * @param saveVO 角色权限保存VO
     * @return 保存成功/失败
     */
    public Result<String> saveRolePrivilege(RolePrivilegeSaveVO saveVO);

}
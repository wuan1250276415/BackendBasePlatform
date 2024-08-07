package pro.wuan.privilege.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.OrgRolePrivilegeModel;
import pro.wuan.privilege.mapper.OrgRolePrivilegeMapper;
import pro.wuan.privilege.service.IOrgRolePrivilegeService;
import pro.wuan.privilege.vo.RolePrivilegeQueryVO;
import pro.wuan.privilege.vo.RolePrivilegeResultVO;
import pro.wuan.privilege.vo.RolePrivilegeSaveVO;

import javax.validation.constraints.NotNull;

/**
 * 角色权限关联表
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@RestController
@RequestMapping("/org/rolePrivilege")
public class OrgRolePrivilegeController extends CURDController<IOrgRolePrivilegeService, OrgRolePrivilegeMapper, OrgRolePrivilegeModel> {

    /**
     * 获取角色授权时的菜单树形结构
     *
     * @param roleId 角色id
     * @return 角色权限返回结果VO
     */
    @AspectLog(description = "获取角色授权时的菜单树形结构", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getMenuTreeByRole")
    public Result<RolePrivilegeResultVO> getMenuTreeByRole(@Validated @NotNull(message = "roleId不能为空") Long roleId) {
        try {
            return service.getMenuTreeByRole(roleId);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取角色授权时的按钮树形结构
     *
     * @param queryVO 角色权限查询VO
     * @return 角色权限返回结果VO
     */
    @AspectLog(description = "获取角色授权时的按钮树形结构", type = OperationType.OPERATION_QUERY)
    @PostMapping("/getButtonTreeByRole")
    public Result<RolePrivilegeResultVO> getButtonTreeByRole(@RequestBody @Validated RolePrivilegeQueryVO queryVO) {
        try {
            return service.getButtonTreeByRole(queryVO);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取角色授权时的列表树形结构
     *
     * @param queryVO 角色权限查询VO
     * @return 角色权限返回结果VO
     */
    @AspectLog(description = "获取角色授权时的列表树形结构", type = OperationType.OPERATION_QUERY)
    @PostMapping("/getListTreeByRole")
    public Result<RolePrivilegeResultVO> getListTreeByRole(@RequestBody @Validated RolePrivilegeQueryVO queryVO) {
        try {
            return service.getListTreeByRole(queryVO);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取角色授权时的数据方案树形结构
     *
     * @param queryVO 角色权限查询VO
     * @return 角色权限返回结果VO
     */
    @AspectLog(description = "获取角色授权时的数据方案树形结构", type = OperationType.OPERATION_QUERY)
    @PostMapping("/getDataSchemeTreeByRole")
    public Result<RolePrivilegeResultVO> getDataSchemeTreeByRole(@RequestBody @Validated RolePrivilegeQueryVO queryVO) {
        try {
            return service.getDataSchemeTreeByRole(queryVO);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 保存角色权限
     *
     * @param saveVO 角色权限保存VO
     * @return 保存成功/失败
     */
    @AspectLog(description = "保存角色权限", type = OperationType.OPERATION_ADD)
    @PostMapping("/saveRolePrivilege")
    public Result<String> saveRolePrivilege(@RequestBody @Validated RolePrivilegeSaveVO saveVO) {
        try {
            return service.saveRolePrivilege(saveVO);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

}

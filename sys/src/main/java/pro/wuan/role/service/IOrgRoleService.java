package pro.wuan.role.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.OrgRoleModel;
import pro.wuan.role.dto.BindingRolePrivilegesDto;
import pro.wuan.role.dto.OrgRoleDto;
import pro.wuan.role.mapper.OrgRoleMapper;
import pro.wuan.role.vo.RoleSortVO;
import pro.wuan.role.vo.RoleUserSaveVO;
import pro.wuan.user.dto.excelImport.ExcelImportResultDto;
import pro.wuan.user.dto.excelImport.ExcelRoleDto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色
 *
 * @author: liumin
 * @date: 2021-08-30 11:15:00
 */
public interface IOrgRoleService extends IBaseService<OrgRoleMapper, OrgRoleModel> {

    /**
     * 获取当前用户可维护的角色列表
     *
     * @param appId 应用id
     * @return 角色实体列表
     */
    public List<RoleSortVO> getManageRoleList(@NotNull(message = "appId不能为空") Long appId);

    /**
     * 批量导入角色
     *
     * @param roleDtoList
     * @param appId
     * @return
     */
    public ExcelImportResultDto batchImportRole(List<ExcelRoleDto> roleDtoList, Long appId);

    /**
     * 保存角色
     *
     * @param roleModel 角色实体
     * @return 角色实体
     */
    public Result<OrgRoleModel> saveRole(OrgRoleModel roleModel);

    /**
     * 更新角色
     *
     * @param roleModel 角色实体
     * @return 角色实体
     */
    public Result<OrgRoleModel> updateRole(OrgRoleModel roleModel);

    /**
     * 删除角色
     *
     * @param id 角色id
     * @return 删除成功/失败
     */
    public Result<String> deleteRole(@NotNull(message = "id不能为空") Long id);

    /**
     * 保存用户角色关联信息
     *
     * @param roleUserSaveVO 角色用户关联保存VO
     * @return 保存成功/失败
     */
    public Result<String> saveUserRoleList(RoleUserSaveVO roleUserSaveVO);

    /**
     * 删除用户角色关联信息
     *
     * @param roleId 角色id
     * @param userId 用户id
     * @return 删除成功/失败
     */
    public Result<String> deleteUserRole(@NotNull(message = "roleId不能为空") Long roleId, @NotNull(message = "userId不能为空") Long userId);

    /**
     * 根据用户id，应用id获取关联的角色列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 角色列表
     */
    public List<OrgRoleModel> getRoleListByUserAndApp(@NotNull(message = "userId不能为空") Long userId, Long appId);

    /**
     * 根据创建用户id，应用id获取关联的角色列表
     *
     * @param createUserId 创建用户id
     * @param appId 应用id
     * @return 角色列表
     */
    public List<OrgRoleModel> getRoleListByCreateUserAndApp(@NotNull(message = "createUserId不能为空") Long createUserId, Long appId);

    /**
     * 根据单位id获取关联的角色列表
     *
     * @param unitId 单位id
     * @param appId 应用id
     * @return 角色列表
     */
    public List<OrgRoleModel> getRoleListByUnitId(@NotNull(message = "unitId不能为空") Long unitId, Long appId);

    /**
     * 根据应用编码获取关联的角色列表
     *
     * @param appCode 应用编码
     * @return 角色列表
     */
    public List<OrgRoleModel> getRoleListByAppCode(@NotBlank(message = "appCode不能为空") String appCode);

    /**
     * 根据角色名称获取角色信息
     *
     * @param name 角色名称
     * @return 角色信息
     */
    public OrgRoleModel getRoleByName(String name);

    /**
     * 编码是否存在
     *
     * @param id 应用id
     * @param code 编码
     * @return true/false
     */
    public boolean isExistCode(Long id, String code);

    /**
     * 根据角色名称获取角色信息
     *
     * @param unitId 单位id
     * @param name 角色名称
     * @return 角色信息
     */
    public OrgRoleModel getRoleByUnitIdAndName(Long unitId,String name);

    /**
     * （新）批量导入角色
     * @author: KevinYu
     * @date: 2023/1/13 9:50
     * @param: [file, appId]
     * @return: pro.wuan.core.model.Result
     */
    Result importRole(MultipartFile file, Long appId);

    /**
     * 绑定多单位角色权限（菜单、按钮、列表、数据权限）
     *
     * @param dto 角色实体
     * @return 角色实体
     */
    Result<OrgRoleModel> bindingUnitsRolePrivileges(BindingRolePrivilegesDto dto);
    /**
     * 批量多单位保存多角色
     *
     * @param dto 角色实体
     * @return 角色实体
     */
    public Result<OrgRoleModel> saveUnitsBindingRoles(OrgRoleDto dto);
}

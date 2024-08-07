package pro.wuan.role.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.wuan.common.core.annotation.constraints.group.ValidatorQueryCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.convertor.db2page.DbConvert2Page;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.OrgRoleModel;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;
import pro.wuan.role.dto.BindingRolePrivilegesDto;
import pro.wuan.role.mapper.OrgRoleMapper;
import pro.wuan.role.service.IOrgRoleService;
import pro.wuan.role.vo.RoleSortVO;
import pro.wuan.role.vo.RoleUserSaveVO;
import pro.wuan.user.service.IOrgUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

/**
 * 角色
 *
 * @author: liumin
 * @date: 2021-08-30 11:15:00
 */
@Slf4j
@RestController
@RequestMapping("/org/role")
public class OrgRoleController extends CURDController<IOrgRoleService, OrgRoleMapper, OrgRoleModel> {

    @Resource
    private IOrgUserService userService;

    /**
     * 获取当前用户可维护的角色列表
     *
     * @param appId 应用id
     * @return 角色实体列表
     */
    @AspectLog(description = "获取当前用户可维护的角色列表", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getManageRoleList")
    public Result<List<RoleSortVO>> getManageRoleList(Long appId) {
        try {
            return Result.success(service.getManageRoleList(appId));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 分页查询角色关联的用户列表
     *
     * @param pageSearchParam 分页查询内容
     * @return 用户分页列表
     */
    @AspectLog(description = "分页查询角色关联的用户列表", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectUserListPageByRole")
    public Result<IPage<OrgUserModel>> selectUserListPageByRole(@RequestBody @Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam) {
        try {
            Result<IPage<OrgUserModel>> result = userService.selectUserListPageByRole(pageSearchParam);
            if (result.getResult() == null) {
                return result;
            }
            List<OrgUserModel> columnModels = result.getResult().getRecords();
            if (CollectionUtil.isNotEmpty(columnModels)) {
                DbConvert2Page dbConvert2Page = new DbConvert2Page();
                dbConvert2Page.executeDbToPage4List(columnModels);
            }
            return result;
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * todo营商专用--分页查询角色关联的用户列表
     *
     * @param pageSearchParam 分页查询内容
     * @return 用户分页列表
     */
    @AspectLog(description = "分页查询角色关联的用户列表", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectUserListPageByRoleOfCbesp")
    public Result<IPage<OrgUserModel>> selectUserListPageByRoleOfCbesp(@RequestBody @Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam) {
        try {
            Result<IPage<OrgUserModel>> result = userService.selectUserListPageByRole(pageSearchParam);
            if (result.getResult() == null) {
                return result;
            }
            List<OrgUserModel> columnModels = result.getResult().getRecords();
            if (CollectionUtil.isNotEmpty(columnModels)) {
                DbConvert2Page dbConvert2Page = new DbConvert2Page();
                dbConvert2Page.executeDbToPage4List(columnModels);
            }
            return result;
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 保存角色
     *
     * @param roleModel 角色实体
     * @return 角色实体
     */
    @AspectLog(description = "保存角色", type = OperationType.OPERATION_ADD)
    @PostMapping("/saveRole")
    public Result<OrgRoleModel> saveRole(@RequestBody @Validated(value = ValidatorSaveCheck.class) OrgRoleModel roleModel) {
        try {
            return service.saveRole(roleModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 更新角色
     *
     * @param roleModel 角色实体
     * @return 角色实体
     */
    @AspectLog(description = "更新角色", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateRole")
    public Result<OrgRoleModel> updateRole(@RequestBody @Validated(value = ValidatorUpdateCheck.class) OrgRoleModel roleModel) {
        try {
            return service.updateRole(roleModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 删除角色
     *
     * @param id 角色id
     * @return 删除成功/失败
     */
    @AspectLog(description = "删除角色", type = OperationType.OPERATION_DELETE)
    @GetMapping("/deleteRole")
    public Result<String> deleteRole(@Validated @NotNull(message = "id不能为空") Long id) {
        try {
            return service.deleteRole(id);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 保存用户角色关联信息
     *
     * @param roleUserSaveVO 角色用户关联保存VO
     * @return 保存成功/失败
     */
    @AspectLog(description = "保存用户角色关联信息", type = OperationType.OPERATION_ADD)
    @PostMapping("/saveUserRoleList")
    public Result<String> saveUserRoleList(@RequestBody @Validated(value = ValidatorSaveCheck.class) RoleUserSaveVO roleUserSaveVO) {
        try {
            return service.saveUserRoleList(roleUserSaveVO);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 删除用户角色关联信息
     *
     * @param roleId 角色id
     * @param userId 用户id
     * @return 删除成功/失败
     */
    @AspectLog(description = "删除用户角色关联信息", type = OperationType.OPERATION_ADD)
    @GetMapping("/deleteUserRole")
    public Result<String> deleteUserRole(@Validated @NotNull(message = "roleId不能为空") Long roleId, @Validated @NotNull(message = "userId不能为空") Long userId) {
        try {
            return service.deleteUserRole(roleId, userId);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 下载角色导入模板
     *
     * @param response 响应
     */
    @AspectLog(description = "下载角色导入模板", type = OperationType.OPERATION_DOWNLOAD)
    @RequestMapping(value = "/downloadRoleImportTemplate")
    public void downloadRoleImportTemplate(HttpServletResponse response) {
        try {
            //获取要下载的模板名称
            String fileName = "importRoleTemplate.xlsx";
            //通知客服文件的MIME类型
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            //设置要下载的文件的名称
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
            //获取文件的路径
            String filePath = Objects.requireNonNull(getClass().getResource("/templates/" + fileName)).getPath();
            filePath = URLDecoder.decode(filePath, "UTF-8");
            FileInputStream input = new FileInputStream(filePath);
            OutputStream out = response.getOutputStream();
            byte[] b = new byte[2048];
            int len;
            while ((len = input.read(b)) != -1) {
                out.write(b, 0, len);
            }
            response.setHeader("Content-Length", String.valueOf(input.getChannel().size()));
            input.close();
        } catch (Exception ex) {
            log.error("下载模板出错:", ex);
        }
    }

    /**
     * 批量导入角色-用于思政教育
     * @author: KevinYu
     * @date: 2023/1/13 9:50
     * @param: [file, appId]
     * @return: pro.wuan.core.model.Result
     */
    @AspectLog(description = "批量导入角色", type = OperationType.OPERATION_ADD)
    @PostMapping(value = "/importRole", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result importRole(@RequestPart("file") MultipartFile file, @RequestParam("appId") Long appId) {
        return service.importRole(file, appId);
    }

    /**
     * 绑定多单位角色权限（菜单、按钮、列表、数据权限）
     *
     * @param dto 角色实体
     * @return 角色实体
     */
    @AspectLog(description = "绑定多单位角色权限", type = OperationType.OPERATION_ADD)
    @PostMapping("/bindingUnitsRolePrivileges")
    public Result<OrgRoleModel> bindingUnitsRolePrivileges(@RequestBody @Validated(value = ValidatorSaveCheck.class) BindingRolePrivilegesDto dto) {
        try {
            return service.bindingUnitsRolePrivileges(dto);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    @AspectLog(description = "获取一条当前应用的角色", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getCurrentAppRoleByAppId")
    public Result<OrgRoleModel> getCurrentAppRoleByAppId(@Validated @NotNull(message = "appId不能为空") Long appId) {
        LambdaQueryWrapper<OrgRoleModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrgRoleModel::getAppId, appId);
        queryWrapper.orderByDesc(OrgRoleModel::getCreateTime);
        List<OrgRoleModel> roleList = service.selectList(queryWrapper);
        if(CollectionUtil.isNotEmpty(roleList) && !roleList.isEmpty()){
            return Result.success(roleList.getFirst());
        }else{
            return Result.failure("未获取到当前应用下的角色！");
        }
    }

}

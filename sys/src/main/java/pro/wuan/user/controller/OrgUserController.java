package pro.wuan.user.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.wuan.common.core.annotation.constraints.group.ValidatorQueryCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateOneselfCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.redis.util.JedisUtil;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.OrgRoleModel;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.feignapi.userapi.vo.UpdateUserOrganAndRoleVo;
import pro.wuan.user.dto.UserPasswordDto;
import pro.wuan.user.mapper.OrgUserMapper;
import pro.wuan.user.service.IOrgUserService;

import javax.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

/**
 * 用户
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class OrgUserController extends CURDController<IOrgUserService, OrgUserMapper, OrgUserModel> {

    @Autowired
    private JedisUtil jedisUtil;

//    /**
//     * 获取用户列表
//     *
//     * @param pageSearchParam
//     * @return
//     */
//    @PostMapping(value = "/selectPage")
//    @ResponseBody
//    public Result<IPage<OrgUserModel>> selectPage(@Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam) {
//        if(StrUtil.isBlank(pageSearchParam.getSortField())){
//            pageSearchParam.setSortField("userName");
//            pageSearchParam.setSortOrder("asc");
//        }
//        return service.selectPage(pageSearchParam);
//    }

    /**
     * 保存用户
     *
     * @param userModel 用户实体
     * @return 用户实体
     */
    @AspectLog(description = "保存用户", type = OperationType.OPERATION_ADD)
    @PostMapping("/saveUser")
    public Result<OrgUserModel> saveUser(@RequestBody @Validated(value = ValidatorSaveCheck.class) OrgUserModel userModel) {
        try {
            return service.saveUser(userModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 更新用户
     *
     * @param userModel 用户实体
     * @return 用户实体
     */
    @AspectLog(description = "更新用户", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateUser")
    public Result<OrgUserModel> updateUser(@RequestBody @Validated(value = ValidatorUpdateCheck.class) OrgUserModel userModel) {
        try {
            return service.updateUser(userModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 删除成功/失败
     */
    @AspectLog(description = "删除用户", type = OperationType.OPERATION_DELETE)
    @GetMapping("/deleteUser")
    public Result<String> deleteUser(@Validated @NotNull(message = "id不能为空") Long id) {
        try {
            if (service.deleteUser(id) > 0) {
                return Result.success("删除成功！");
            } else {
                return Result.failure("删除失败！");
            }
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取组织机构下当前用户可授权的角色列表
     *
     * @param organId 组织机构id
     * @return 角色实体列表
     */
    @AspectLog(description = "获取组织机构下当前用户可授权的角色列表", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getRoleListByOrganAndCurrentUser")
    public Result<List<OrgRoleModel>> getRoleListByOrganAndCurrentUser(@Validated @NotNull(message = "organId不能为空") Long organId) {
        try {
            return Result.success(service.getRoleListByOrganAndCurrentUser(organId));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取组织机构所属单位下的用户列表
     *
     * @param organId 组织机构id
     * @return 用户实体列表
     */
    @AspectLog(description = "获取组织机构所属单位下的用户列表", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getUserListByOrgan")
    public Result<List<OrgUserModel>> getUserListByOrgan(@Validated @NotNull(message = "organId不能为空") Long organId) {
        try {
            return Result.success(service.getUserListByOrgan(organId));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取组织机构下的用户列表
     *
     * @param organId 组织机构id
     * @return 用户实体列表
     */
    @AspectLog(description = "获取组织机构下的用户列表", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getUserListByOrganId")
    public Result<List<OrgUserModel>> getUserListByOrganId(@Validated @NotNull(message = "organId不能为空") Long organId) {
        try {
            return Result.success(service.getUserListByOrganId(organId));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取组织机构所属单位下的用户列表（排除userId对应的用户）
     *
     * @param organId 组织机构id
     * @param userId  用户id
     * @return 用户实体列表
     */
    @AspectLog(description = "获取组织机构所属单位下的用户列表（排除userId对应的用户）", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getUserListByOrganAndParent")
    public Result<List<OrgUserModel>> getUserListByUnit(@Validated @NotNull(message = "organId不能为空") Long organId, Long userId) {
        try {
            return Result.success(service.getUserListByUnit(organId, userId));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }


    /**
     * 修改密码
     *
     * @param userPasswordDto 封装修改新旧密码的对象
     * @return
     */
    @AspectLog(description = "修改密码", type = OperationType.OPERATION_UPDATE)
    @RequestMapping("/updatePassword")
    @ResponseBody
    public Result updatePassword(@Validated @RequestBody UserPasswordDto userPasswordDto) {
        return service.updatePassword(userPasswordDto);
    }


    /**
     * 重置密码
     *
     * @param userId
     * @return
     */
    @AspectLog(description = "重置密码", type = OperationType.OPERATION_UPDATE)
    @GetMapping("/rest/password")
    @ResponseBody
    public Result restPassword(@NotBlank @RequestParam("userId") Long userId, @NotBlank @RequestParam("password") String password) {
        return service.restPassword(userId, password);
    }


    /**
     * 系统（单位）锁定用户列表
     *
     * @param pageSearchParam
     * @return
     */
    @AspectLog(description = "系统（单位）锁定用户列表", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectLockUsers")
    @ResponseBody
    public Result<IPage<OrgUserModel>> selectLockUsers(@Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam) {
        QueryWrapper<OrgUserModel> queryWrapper = new QueryWrapper();
        if (UserContext.getCurrentUserTenantId() != null && UserContext.getCurrentUserTenantId() != 0L) {
            queryWrapper.eq("tenant_id", UserContext.getCurrentUserTenantId());
        }
        queryWrapper.isNotNull("lock_time");
        pageSearchParam.setWrapper(queryWrapper);
        return service.selectPage(pageSearchParam);
    }


    /**
     * 修改用户信息(自己修改)
     *
     * @param userModel 用户实体
     * @return 用户实体
     */
    @AspectLog(description = "修改用户信息(自己修改)", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateOneself")
    public Result<OrgUserModel> updateOneself(@RequestBody @Validated(value = ValidatorUpdateOneselfCheck.class) OrgUserModel userModel) {
        if (service.updateById(userModel) == 1) {
            OrgUserModel user = service.selectById(userModel.getId());
            // 获取用户信息线程上下文
            UserDetails userDetails = UserContext.getCurrentUser();
            userDetails.setUser(user);
            //设置缓存信息
            jedisUtil.set(CommonConstant.JEDIS_USER_DETAILS_PREFIX + userDetails.getAccount(), userDetails);
            return Result.success();
        } else {
            return Result.failure();
        }
    }

    /**
     * 批量导入用户
     *
     * @param file
     * @param organId
     * @param roleIdList
     * @return
     */
    @AspectLog(description = "批量导入用户", type = OperationType.OPERATION_ADD)
    @PostMapping(value = "/batchImportUser", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result batchImportUser(@RequestPart("file") MultipartFile file, @RequestParam("organId") Long organId, @RequestParam("roleIdList") List<Long> roleIdList) {
        return service.batchImportUser(file, organId, roleIdList);
    }

    /**
     * 更新用户所属的组织机构信息及关联的角色
     *
     * @param vo 更新用户所属的组织机构信息及关联的角色vo
     * @return
     */
    @AspectLog(description = "更新用户所属的组织机构信息及关联的角色", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateUserOrganAndRole")
    public Result<String> updateUserOrganAndRole(@RequestBody UpdateUserOrganAndRoleVo vo) {
        return service.updateUserOrganAndRole(vo) ? Result.success("用户信息更新成功") : Result.failure("用户信息更新失败");
    }

    /**
     * 下载用户导入模板
     *
     * @param response 响应
     */
    @AspectLog(description = "下载用户导入模板", type = OperationType.OPERATION_DOWNLOAD)
    @RequestMapping(value = "/downloadUnitUserRoleImportTemplate")
    public void downloadUnitUserRoleImportTemplate(HttpServletResponse response) {
        try {
            //获取要下载的模板名称
            String fileName = "importUserTemplate.xlsx";
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
     * 批量导入用户
     *
     * @param file
     * @param appId
     * @return
     */
    @AspectLog(description = "批量导入用户", type = OperationType.OPERATION_ADD)
    @PostMapping(value = "/batchImportUnitUserRole", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result batchImportUnitUserRole(@RequestPart("file") MultipartFile file, @RequestParam("appId") Long appId) {
        return service.batchImportUnitUserRole(file, appId);
    }

    /**
     * todo营商专用-分页获取用户列表
     *
     * @param pageSearchParam
     * @return
     */
    @PostMapping(value = "/selectUserPage")
    @ResponseBody
    public Result<IPage<OrgUserModel>> selectUserPage(@Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam) {
        return service.selectPage(pageSearchParam);
    }


//    /**
//     * 根据单位名称获取用户列表
//     *
//     * @param unitName 根据单位名称获取用户列表
//     * @return
//     */
//    @AspectLog(description = "根据单位名称获取用户列表", type = OperationType.OPERATION_QUERY)
//    @GetMapping("/getUsersByUnitName")
//    public Result getUsersByUnitName(@RequestParam("unitName") String unitName) {
//        try {
//            JoinLambdaWrapper<OrgUserModel> wrapper = new JoinLambdaWrapper<>(OrgUserModel.class);
//            wrapper.leftJoin(OrgOrganModel.class, OrgOrganModel::getId, OrgUserModel::getOrganId)
//                    .eq(OrgOrganModel::getId, unitName)
//                    .selectAll().end();
//
//
//            return Result.success(service.joinList(wrapper, OrgUserModel.class));
//        } catch (Exception e) {
//            return Result.failure(ExceptionUtil.getMessage(e));
//        }
//    }

}

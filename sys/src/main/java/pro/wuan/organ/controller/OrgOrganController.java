package pro.wuan.organ.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.wuan.common.core.annotation.constraints.group.ValidatorQueryCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.constant.SearchOptConstant;
import pro.wuan.common.core.model.BaseQueryValue;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;
import pro.wuan.feignapi.userapi.group.ValidatorUnitSaveCheck;
import pro.wuan.feignapi.userapi.group.ValidatorUnitUpdateCheck;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.feignapi.userapi.vo.OrganTreeVO;
import pro.wuan.feignapi.userapi.vo.UnitTreeVO;
import pro.wuan.organ.mapper.OrgOrganMapper;
import pro.wuan.organ.service.IOrgOrganService;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

/**
 * 组织机构
 *
 * @author: liumin
 * @date: 2021-08-30 11:21:11
 */
@Slf4j
@RestController
@RequestMapping("/org/organ")
public class OrgOrganController extends CURDController<IOrgOrganService, OrgOrganMapper, OrgOrganModel> {

    /**
     * 分页查询实体
     *
     * @param pageSearchParam
     * @return
     */
    @AspectLog(description = "分页查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectPage")
    @ResponseBody
    @Override
    public Result<IPage<OrgOrganModel>> selectPage(@RequestBody @Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam) {
        String key = "parentId";
        if (pageSearchParam.peek(key) == null) {
            UserDetails userDetails = UserContext.getCurrentUser();
            OrgUserModel user = userDetails.getUser();
            if (user == null) {
                return Result.failure("当前用户信息为空");
            }
            if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 超级管理员
                return service.selectPage(pageSearchParam);
            } else {
                BaseQueryValue queryValue = new BaseQueryValue();
                queryValue.setOpt(SearchOptConstant.SEARCH_EQUAL);
                OrgOrganModel unit = userDetails.getUnit();
                if (unit != null) {
                    queryValue.setValues(new Object[]{unit.getId()});
                } else {
                    queryValue.setValues(new Object[]{CommonConstant.DEFAULT_ID});
                }
                pageSearchParam.addQueryParams(key, queryValue);
            }
        }
        return service.selectPage(pageSearchParam);
    }

    /**
     * 分页查询实体
     *
     * @param pageSearchParam
     * @return
     */
    @AspectLog(description = "分页查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectAllPage")
    @ResponseBody
    public Result<IPage<OrgOrganModel>> selectAllPage(@RequestBody @Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam) {
        return service.selectPage(pageSearchParam);
    }

    /**
     * 获取单位树形结构，只显示单位结点
     *
     * @param id       组织机构id，值为空时根据当前登录用户获取单位树形结构
     * @param showRoot 是否显示顶级结点
     * @return 单位树形结构
     */
    @AspectLog(description = "获取单位树形结构", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getUnitTree")
    public Result<List<UnitTreeVO>> getUnitTree(Long id, @Validated @NotNull(message = "showRoot不能为空") boolean showRoot) {
        try {
            return Result.success(service.getUnitTree(id, showRoot));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取单位树形结构，只显示单位结点
     *
     * @return 所有单位树形结构
     */
    @AspectLog(description = "获取所有单位树形结构", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getAllUnitTree")
    public Result<List<UnitTreeVO>> getAllUnitTree() {
        try {
            return Result.success(service.getAllUnitTree());
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 保存单位
     *
     * @param organModel 单位实体
     * @return 单位实体
     */
    @AspectLog(description = "保存单位", type = OperationType.OPERATION_ADD)
    @PostMapping("/saveUnit")
    public Result<OrgOrganModel> saveUnit(@RequestBody @Validated(value = {ValidatorSaveCheck.class, ValidatorUnitSaveCheck.class}) OrgOrganModel organModel) {
        try {
            return service.saveUnit(organModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 更新单位
     *
     * @param organModel 单位实体
     * @return 单位实体
     */
    @AspectLog(description = "更新单位", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateUnit")
    public Result<OrgOrganModel> updateUnit(@RequestBody @Validated(value = {ValidatorUpdateCheck.class, ValidatorUnitUpdateCheck.class}) OrgOrganModel organModel) {
        try {
            return service.updateUnit(organModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 删除单位
     *
     * @param id 单位id
     * @return 删除成功/失败
     */
    @AspectLog(description = "删除单位", type = OperationType.OPERATION_DELETE)
    @GetMapping("/deleteUnit")
    public Result<String> deleteUnit(@Validated @NotNull(message = "id不能为空") Long id) {
        try {
            return service.deleteUnit(id);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 启用单位
     *
     * @param id 单位id
     * @return 单位实体
     */
    @AspectLog(description = "启用单位", type = OperationType.OPERATION_UPDATE)
    @GetMapping("/useUnit")
    public Result<OrgOrganModel> useUnit(@Validated @NotNull(message = "id不能为空") Long id) {
        try {
            return service.useUnit(id);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 停用单位
     *
     * @param id 单位id
     * @return 单位实体
     */
    @AspectLog(description = "停用单位", type = OperationType.OPERATION_UPDATE)
    @GetMapping("/nonuseUnit")
    public Result<OrgOrganModel> nonuseUnit(@Validated @NotNull(message = "id不能为空") Long id) {
        try {
            return service.nonuseUnit(id);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取组织机构树形结构
     *
     * @param id       组织机构id，值为空时根据当前登录用户获取从单位开始的组织机构树形结构
     * @param showRoot 是否显示顶级结点
     * @return 组织机构树形结构
     */
    @AspectLog(description = "获取组织机构树形结构", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getOrganTree")
    public Result<List<OrganTreeVO>> getOrganTree(Long id, @Validated @NotNull(message = "showRoot不能为空") boolean showRoot) {
        try {
            return Result.success(service.getOrganTree(id, showRoot));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 保存部门
     *
     * @param organModel 部门实体
     * @return 部门实体
     */
    @AspectLog(description = "保存部门", type = OperationType.OPERATION_ADD)
    @PostMapping("/saveDept")
    public Result<OrgOrganModel> saveDept(@RequestBody @Validated(value = ValidatorSaveCheck.class) OrgOrganModel organModel) {
        try {
            return service.saveDept(organModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 更新部门
     *
     * @param organModel 部门实体
     * @return 部门实体
     */
    @AspectLog(description = "更新部门", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateDept")
    public Result<OrgOrganModel> updateDept(@RequestBody @Validated(value = ValidatorUpdateCheck.class) OrgOrganModel organModel) {
        try {
            return service.updateDept(organModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 删除部门
     *
     * @param id 部门id
     * @return 删除成功/失败
     */
    @AspectLog(description = "删除部门", type = OperationType.OPERATION_DELETE)
    @GetMapping("/deleteDept")
    public Result<String> deleteDept(@Validated @NotNull(message = "id不能为空") Long id) {
        try {
            return service.deleteDept(id);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 根据单位类型获取单位列表
     *
     * @param organType 单位类型
     * @return
     */
    @AspectLog(description = "根据单位类型获取单位列表", type = OperationType.OPERATION_DELETE)
    @GetMapping("/getUnitListByType")
    public Result getUnitListByType(@RequestParam("organType") String organType) {
        try {
            return Result.success(service.getUnitListByType(organType));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 下载单位导入模板
     *
     * @param response 响应
     */
    @AspectLog(description = "下载单位导入模板", type = OperationType.OPERATION_DOWNLOAD)
    @RequestMapping(value = "/downloadUnitImportTemplate")
    public void downloadUnitImportTemplate(HttpServletResponse response) {
        try {
            //获取要下载的模板名称
            String fileName = "importUnitTemplate.xlsx";
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
     * 批量导入单位-用于思政教育
     * @author: KevinYu
     * @date: 2023/1/13 9:50
     * @param: [file, appId]
     * @return: pro.wuan.core.model.Result
     */
    @AspectLog(description = "批量导入单位", type = OperationType.OPERATION_ADD)
    @PostMapping(value = "/batchImportUnit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result batchImportUnit(@RequestPart("file") MultipartFile file, @RequestParam("appId") Long appId) {
        return service.importExcelUnit(file, appId);
    }

}

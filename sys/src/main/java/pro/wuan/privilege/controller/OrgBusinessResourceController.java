package pro.wuan.privilege.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.BaseSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.utils.ConvertUtil;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.OrgBusinessResourceModel;
import pro.wuan.feignapi.userapi.group.ValidatorFunctionSaveCheck;
import pro.wuan.feignapi.userapi.group.ValidatorFunctionUpdateCheck;
import pro.wuan.feignapi.userapi.group.ValidatorMenuSaveCheck;
import pro.wuan.feignapi.userapi.group.ValidatorMenuUpdateCheck;
import pro.wuan.privilege.mapper.OrgBusinessResourceMapper;
import pro.wuan.privilege.service.IOrgBusinessResourceService;
import pro.wuan.privilege.vo.MenuTreeListQueryParamVO;
import pro.wuan.privilege.vo.ResourceTreeVO;

import java.util.List;

/**
 * 业务资源
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@RestController
@RequestMapping("/org/businessResource")
public class OrgBusinessResourceController extends CURDController<IOrgBusinessResourceService, OrgBusinessResourceMapper, OrgBusinessResourceModel> {

    /**
     * 查询菜单树形列表。包括参数：appId（应用id），name（名称），status（显示状态。枚举：0-隐藏，1-显示）
     *
     * @param searchParam 查询参数不能为空
     * @return 菜单树形列表
     */
    @AspectLog(description = "查询菜单树形列表", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectMenuTreeList")
    public Result<List<OrgBusinessResourceModel>> selectMenuTreeList(@RequestBody BaseSearchParam searchParam) {
        try {
            if (searchParam == null || CollectionUtil.isEmpty(searchParam.getQueryPrams())) {
                return Result.failure("查询参数不能为空");
            }
            MenuTreeListQueryParamVO queryParamVO = ConvertUtil.queryMapToObject(searchParam.getQueryPrams(), MenuTreeListQueryParamVO.class);
            if (queryParamVO.getAppId() == null) {
                return Result.failure("应用id不能为空");
            }
            return Result.success(service.selectMenuTreeList(queryParamVO));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取菜单树形结构（用于菜单编辑页面选择上级菜单）
     *
     * @param appId 应用id
     * @return 菜单树形结构
     */
    @AspectLog(description = "获取菜单树形结构（用于菜单编辑页面选择上级菜单）", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getMenuTree")
    public Result<ResourceTreeVO> getMenuTree(Long appId) {
        try {
            return Result.success(service.getMenuTree(appId));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 保存菜单/目录
     *
     * @param resourceModel 业务资源实体
     * @return 业务资源实体
     */
    @AspectLog(description = "保存菜单/目录", type = OperationType.OPERATION_ADD)
    @PostMapping("/saveMenu")
    public Result<OrgBusinessResourceModel> saveMenu(@RequestBody @Validated(value = {ValidatorSaveCheck.class, ValidatorMenuSaveCheck.class})
                                                                 OrgBusinessResourceModel resourceModel) {
        try {
            this.checkWhenEditMenu(resourceModel);
            return this.save(resourceModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 更新菜单/目录
     *
     * @param resourceModel 业务资源实体
     * @return 业务资源实体
     */
    @AspectLog(description = "更新菜单/目录", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateMenu")
    public Result<OrgBusinessResourceModel> updateMenu(@RequestBody @Validated(value = {ValidatorUpdateCheck.class, ValidatorMenuUpdateCheck.class})
                                                                   OrgBusinessResourceModel resourceModel) {
        try {
            this.checkWhenEditMenu(resourceModel);
            return service.updateMenu(resourceModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 编辑菜单/目录时数据校验
     *
     * @param resourceModel
     * @return void
     */
    private void checkWhenEditMenu(OrgBusinessResourceModel resourceModel) {
        Assert.hasLength(resourceModel.getType(), "类型不能为空");
        Assert.hasLength(resourceModel.getIcon(), "图标不能为空");
        if (CommonConstant.BUSINESS_RESOURCE_TYPE.MENU.getValue().equals(resourceModel.getType())) {
            Assert.hasLength(resourceModel.getUrl(), "地址不能为空");
            Assert.hasLength(resourceModel.getOpenMode(), "打开方式不能为空");
        }
        if (StrUtil.isNotEmpty(resourceModel.getCode())) {
            Assert.isTrue(!service.isExistCode(resourceModel.getId(), resourceModel.getCode()), "编码已经存在，请重新输入");
        }
        Assert.notNull(resourceModel.getAppId(), "应用id不能为空");
    }

    /**
     * 删除菜单/目录
     *
     * @param id 业务资源id
     * @return 删除成功/失败
     */
    @AspectLog(description = "删除菜单/目录", type = OperationType.OPERATION_DELETE)
    @GetMapping("/deleteMenu")
    public Result<String> deleteMenu(Long id) {
        try {
            return service.deleteMenu(id);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 查询功能树形列表
     *
     * @param menuResourceId 菜单资源id
     * @return 功能树形列表
     */
    @AspectLog(description = "查询功能树形列表", type = OperationType.OPERATION_QUERY)
    @GetMapping(value = "/selectFunctionTreeList")
    public Result<List<OrgBusinessResourceModel>> selectFunctionTreeList(Long menuResourceId) {
        try {
            return Result.success(service.selectFunctionTreeList(menuResourceId));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取功能树形结构（按钮，用于菜单编辑页面选择上级菜单）
     *
     * @param menuResourceId 菜单资源id
     * @return 功能树形结构（按钮）
     */
    @AspectLog(description = "获取功能树形结构（按钮，用于菜单编辑页面选择上级菜单）", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getFunctionTree")
    public Result<ResourceTreeVO> getFunctionTree(Long menuResourceId) {
        try {
            return Result.success(service.getFunctionTree(menuResourceId));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 保存功能（按钮）
     *
     * @param resourceModel 业务资源实体
     * @return 业务资源实体
     */
    @AspectLog(description = "保存功能（按钮）", type = OperationType.OPERATION_ADD)
    @PostMapping("/saveFunction")
    public Result<OrgBusinessResourceModel> saveFunction(@RequestBody @Validated(value = {ValidatorSaveCheck.class, ValidatorFunctionSaveCheck.class})
                                                                     OrgBusinessResourceModel resourceModel) {
        try {
            OrgBusinessResourceModel menu = this.checkWhenEditFunctionVTwo(resourceModel);
            resourceModel.setType(CommonConstant.BUSINESS_RESOURCE_TYPE.FUNCTION.getValue());
            resourceModel.setAppId(menu.getAppId());
            return this.save(resourceModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 更新功能（按钮）
     *
     * @param resourceModel 业务资源实体
     * @return 业务资源实体
     */
    @AspectLog(description = "更新功能（按钮）", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateFunction")
    public Result<OrgBusinessResourceModel> updateFunction(@RequestBody @Validated(value = {ValidatorUpdateCheck.class, ValidatorFunctionUpdateCheck.class})
                                                                       OrgBusinessResourceModel resourceModel) {
        try {
            OrgBusinessResourceModel menu = this.checkWhenEditFunctionVTwo(resourceModel);
            OrgBusinessResourceModel function = service.selectById(resourceModel.getId());
            Assert.notNull(function, "功能信息不存在");
            Assert.isTrue(menu.getId().equals(function.getMenuResourceId()), "关联的菜单不允许修改");
            return this.update(resourceModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 编辑功能（按钮）时数据校验
     *
     * @param resourceModel
     * @return
     */
    private OrgBusinessResourceModel checkWhenEditFunction(OrgBusinessResourceModel resourceModel) {
        Assert.hasLength(resourceModel.getCode(), "编码不能为空");
        Assert.notNull(resourceModel.getMenuResourceId(), "菜单资源id不能为空");
        Assert.isTrue(!service.isExistCode(resourceModel.getId(), resourceModel.getCode()), "编码已经存在，请重新输入");
        OrgBusinessResourceModel menu = service.selectById(resourceModel.getMenuResourceId());
        Assert.notNull(menu, "菜单资源不存在");
        Assert.notNull(menu.getAppId(), "菜单资源关联的应用id不能为空");
        Assert.isTrue(CommonConstant.BUSINESS_RESOURCE_TYPE.MENU.getValue().equals(menu.getType()), "只能关联菜单资源");
        return menu;
    }

    /**
     * 编辑功能（按钮）时数据校验VTwo
     *
     * @param resourceModel
     * @return
     */
    private OrgBusinessResourceModel checkWhenEditFunctionVTwo(OrgBusinessResourceModel resourceModel) {
        Assert.hasLength(resourceModel.getCode(), "编码不能为空");
        Assert.notNull(resourceModel.getMenuResourceId(), "菜单资源id不能为空");
        Assert.isTrue(!service.isExistCodeFunction(resourceModel.getId(), resourceModel.getCode(),resourceModel.getMenuResourceId()), "编码已经存在，请重新输入");
        OrgBusinessResourceModel menu = service.selectById(resourceModel.getMenuResourceId());
        Assert.notNull(menu, "菜单资源不存在");
        Assert.notNull(menu.getAppId(), "菜单资源关联的应用id不能为空");
        Assert.isTrue(CommonConstant.BUSINESS_RESOURCE_TYPE.MENU.getValue().equals(menu.getType()), "只能关联菜单资源");
        return menu;
    }

    /**
     * 删除功能（按钮）
     *
     * @param id 业务资源id
     * @return 删除成功/失败
     */
    @AspectLog(description = "删除功能（按钮）", type = OperationType.OPERATION_DELETE)
    @GetMapping("/deleteFunction")
    public Result<String> deleteFunction(Long id) {
        try {
            return service.deleteFunction(id);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

}

package pro.wuan.privilege.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.OrgBusinessResourceModel;
import pro.wuan.feignapi.userapi.entity.OrgListColumnModel;
import pro.wuan.privilege.mapper.OrgListColumnMapper;
import pro.wuan.privilege.service.IOrgBusinessResourceService;
import pro.wuan.privilege.service.IOrgListColumnService;
import pro.wuan.privilege.vo.BatchSaveListColumnVO;

import java.util.List;

/**
 * 列表字段
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@RestController
@RequestMapping("/org/listColumn")
public class OrgListColumnController extends CURDController<IOrgListColumnService, OrgListColumnMapper, OrgListColumnModel> {

    @Autowired
    private IOrgBusinessResourceService businessResourceService;

    /**
     * 保存列表字段
     *
     * @param listColumnModel 列表字段实体
     * @return 列表字段实体
     */
    @AspectLog(description = "保存列表字段", type = OperationType.OPERATION_ADD)
    @PostMapping("/saveListColumn")
    public Result<OrgListColumnModel> saveListColumn(@RequestBody @Validated(value = ValidatorSaveCheck.class) OrgListColumnModel listColumnModel) {
        try {
            this.checkWhenEditListColumn(listColumnModel);
            return this.save(listColumnModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 更新列表字段
     *
     * @param listColumnModel 列表字段实体
     * @return 列表字段实体
     */
    @AspectLog(description = "更新列表字段", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateListColumn")
    public Result<OrgListColumnModel> updateListColumn(@RequestBody @Validated(value = ValidatorUpdateCheck.class) OrgListColumnModel listColumnModel) {
        try {
            Assert.notNull(listColumnModel.getId(), "id不能为空");
            this.checkWhenEditListColumn(listColumnModel);
            return this.update(listColumnModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 编辑列表字段时数据校验
     *
     * @param listColumnModel
     * @return void
     */
    private void checkWhenEditListColumn(OrgListColumnModel listColumnModel) {
        Assert.notNull(listColumnModel.getMenuResourceId(), "菜单资源id不能为空");
        OrgBusinessResourceModel menu = businessResourceService.selectById(listColumnModel.getMenuResourceId());
        Assert.notNull(menu, "菜单资源不存在");
        Assert.notNull(menu.getAppId(), "菜单资源关联的应用id不能为空");
        Assert.isTrue(CommonConstant.BUSINESS_RESOURCE_TYPE.MENU.getValue().equals(menu.getType()), "只能关联菜单资源");
        Assert.isTrue(!service.isExistColumnName(listColumnModel.getId(), listColumnModel.getMenuResourceId(), listColumnModel.getColumnName()),"字段名称已经存在，请重新输入");
    }

    /**
     * 批量新增字段实体
     *
     * @param batchSaveListColumnVO 批量新增字段实体VO
     * @return 新增成功/失败
     */
    @AspectLog(description = "批量新增字段实体", type = OperationType.OPERATION_ADD)
    @PostMapping("/batchSaveListColumn")
    public Result<String> batchSaveListColumn(@RequestBody BatchSaveListColumnVO batchSaveListColumnVO) {
        try {
            return service.batchSaveListColumn(batchSaveListColumnVO);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 根据菜单url获取当前用户的列表字段列表
     *
     * @param url 菜单url
     * @return 列表字段列表
     */
    @AspectLog(description = "根据菜单url获取当前用户的列表字段列表", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getListColumnListByCurrentUser")
    public Result<List<OrgListColumnModel>> getListColumnListByCurrentUser(@NotBlank(message = "url不能为空") String url) {
        try {
            return Result.success(service.getListColumnListByCurrentUser(url));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

}

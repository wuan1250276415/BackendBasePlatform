package pro.wuan.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.core.annotation.constraints.group.ValidatorQueryCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.appapi.OrgAppModel;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.mapper.OrgAppMapper;
import pro.wuan.service.IOrgAppService;

import java.util.List;

/**
 * 应用
 *
 * @author: liumin
 * @date: 2021-08-30 11:22:48
 */
@RestController
@RequestMapping("/org/app")
public class OrgAppController extends CURDController<IOrgAppService, OrgAppMapper, OrgAppModel> {

    /**
     * 根据应用属性查询应用实体信息
     *
     * @param appModel 应用实体
     * @return
     */
    @AspectLog(description = "根据应用属性查询应用实体", type = OperationType.OPERATION_QUERY)
    @PostMapping("/selectByProperty")
    public Result<List<OrgAppModel>> selectByProperty(@Validated(value = ValidatorQueryCheck.class) OrgAppModel appModel) {
        try {
            LambdaQueryWrapper<OrgAppModel> queryWrapper = new LambdaQueryWrapper<>(appModel);
//            queryWrapper.orderByAsc(OrgAppModel::getSort);
            List<OrgAppModel> appModels = getBaseService().selectList(queryWrapper);
            db2pageList(appModels);
            return Result.success(appModels);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 保存应用
     *
     * @param appModel 应用实体
     * @return 应用实体
     */
    @AspectLog(description = "保存应用", type = OperationType.OPERATION_ADD)
    @PostMapping("/saveApp")
    public Result<OrgAppModel> saveApp(@RequestBody @Validated(value = ValidatorSaveCheck.class) OrgAppModel appModel) {
        try {
            Assert.isTrue(!service.isExistCode(appModel.getId(), appModel.getCode()), "编码已经存在，请重新输入");
            if (service.insert(appModel) > 0) {
                return Result.success(appModel);
            } else {
                return Result.failure(appModel);
            }
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 更新应用
     *
     * @param appModel 应用实体
     * @return 应用实体
     */
    @AspectLog(description = "更新应用", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateApp")
    public Result<OrgAppModel> updateApp(@RequestBody @Validated(value = ValidatorUpdateCheck.class) OrgAppModel appModel) {
        try {
            Assert.notNull(appModel.getId(), "id不能为空");
            Assert.isTrue(!service.isExistCode(appModel.getId(), appModel.getCode()), "编码已经存在，请重新输入");
            if (service.updateById(appModel) == 1) {
                return Result.success(appModel);
            } else {
                return Result.failure(appModel);
            }
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取当前用户的应用列表
     *
     * @return 应用列表
     */
    @AspectLog(description = "获取当前用户的应用列表", type = OperationType.OPERATION_QUERY)
    @PostMapping("/getAppListByCurrentUser")
    public Result<List<OrgAppModel>> getAppListByCurrentUser() {
        try {
            OrgUserModel user = UserContext.getCurrentUser().getUser();
            if (user == null) {
                return Result.failure("当前用户信息不能为空");
            }
            List<OrgAppModel> appModels = service.getAppListByUserId(user.getId());
            return Result.success(appModels);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

}

package pro.wuan.common.web.controller;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;

/**
 * @program: tellhowcloud
 * @description: 实体更新操作
 * @author: HawkWang
 * @create: 2021-08-25 13:44
 */
public interface UpdateController<Service extends IBaseService, Mapper extends IBaseMapper, IdModel extends IDModel> extends IBaseController<Mapper, IdModel> {

    /**
     * 修改部分字段
     *
     * @param idModel
     * @return
     */
    @AspectLog(description = "根据ID更新实体有值属性", type = OperationType.OPERATION_UPDATE)
    @PostMapping(value = "/updateById")
    @ResponseBody
    default Result<IdModel> update(@RequestBody @Validated(value = ValidatorUpdateCheck.class) IdModel idModel) {
        if (getBaseService().updateById(idModel) == 1) {
            return Result.success(idModel);
        } else {
            return Result.failure(idModel);
        }

    }

    /**
     * 修改所有字段
     *
     * @param idModel 实体
     * @return
     */
    @AspectLog(description = "根据ID更新实体全部属性", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateAllById")
    @ResponseBody
    default Result<IdModel> updateAll(@RequestBody @Validated IdModel idModel) {
        if (getBaseService().updateAllById(idModel) == 1) {
            return Result.success(idModel);
        } else {
            return Result.failure(idModel);
        }
    }
}

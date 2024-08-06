package pro.wuan.common.web.controller;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;

/**
 * @program: tellhowcloud
 * @description: 保存控制器的接口实现
 * @author: HawkWang
 * @create: 2021-08-25 13:44
 *
 */
public interface SaveController<Service extends IBaseService, Mapper extends IBaseMapper, IdModel extends IDModel> extends IBaseController<Mapper, IdModel> {

    /**
     * 通用的保存方法
     * @param idModel 泛型实体对象 根据Controller实现类确定泛型
     * @return
     */
    @AspectLog(description = "新增实体", type = OperationType.OPERATION_ADD)
    @PostMapping(value = "/save")
    @ResponseBody
    default Result<IdModel> save(@RequestBody @Validated(value = ValidatorSaveCheck.class) IdModel idModel) {
        if (getBaseService().insert(idModel) > 0) {
            return Result.success(idModel);
        } else {
            return Result.failure(idModel);
        }

    }

}

package pro.wuan.common.web.controller;


import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: tellhowcloud
 * @description: Controller的基础接口
 * @author: HawkWang
 * @create: 2021-08-25 13:44
 * 实体删除操作
 */
public interface DeleteController<Service extends IBaseService, Mapper extends IBaseMapper, IdModel extends IDModel> extends IBaseController<Mapper, IdModel> {

    /**
     * 根据ID删除实体
     * @param id 实体的ID
     * @return 删除成功/失败
     */
    @AspectLog(description = "根据ID删除实体", type = OperationType.OPERATION_DELETE)
    @GetMapping(value = "/delete/{id}")
    @ResponseBody
    default Result<String> delete(@PathVariable("id") Long id) {
        if (getBaseService().deleteById(id) > 0) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

    /**
     * 批量删除
     *
     * @param ids 逗号分割的id字符串
     * @return 失败或者成功的结果
     */
    @AspectLog(description = "根据Ids批量删除实体", type = OperationType.OPERATION_DELETE)
    @GetMapping("/deleteBatchIds")
    @ResponseBody
    default Result<String> deleteBatchIds(String ids) {
        Assert.notNull(ids, "请传入参数！");
        List _ids = new ArrayList<>(Arrays.asList(ids.split(",")));
        if (getBaseService().deleteBatchIds(_ids) > 0) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

}

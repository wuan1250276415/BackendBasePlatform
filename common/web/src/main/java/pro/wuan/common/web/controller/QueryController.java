package pro.wuan.common.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.BaseQueryValue;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.constant.ConstantUser;

import java.util.List;
import java.util.Map;

/**
 * @program: tellhowcloud
 * @description: 实体查询操作
 * @author: HawkWang
 * @create: 2021-08-25 13:44
 * 实体删除操作
 */
public interface QueryController<Service extends IBaseService, Mapper extends IBaseMapper, IdModel extends IDModel> extends IBaseController<Mapper, IdModel> {


    /**
     * 根据ID查询信息，根据实体注解进行转换
     *
     * @param id
     * @return
     */
    @AspectLog(description = "根据ID查询实体", type = OperationType.OPERATION_QUERY)
    @GetMapping("/selectById/{id}")
    @ResponseBody
    default Result<IdModel> selectById(@PathVariable Long id) {
        IdModel idModel = getBaseService().selectById(id);
        db2page(idModel);
        return Result.success(idModel);
    }

    /**
     * 根据id集合获取列表
     *
     * @param ids
     * @return
     */
    @GetMapping("/selectBatchByIds")
    default Result<List<IdModel>> selectBatchByIds(@RequestParam("ids") List<Long> ids) {
        List<IdModel> idModels = getBaseService().selectBatchIds(ids);
        db2pageList(idModels);
        return Result.success(idModels);
    }


    /**
     * 根据实体属性查询实体信息
     *
     * @param idModel
     * @return
     */
    @AspectLog(description = "根据实体属性查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping("/selectByProperty")
    @ResponseBody
    default Result<List<IdModel>> selectByProperty(@RequestBody IdModel idModel) {
        QueryWrapper<IdModel> queryWrapper = new QueryWrapper<>(idModel);
        List<IdModel> idModels = getBaseService().selectList(queryWrapper);
        db2pageList(idModels);
        return Result.success(idModels);
    }

    /**
     * 根据查询条件返回所有符合查询结果的数据，并转换
     *
     * @param queryPrams
     * @return
     */
    @AspectLog(description = "不分页查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectByParam")
    @ResponseBody
    default Result<List<IdModel>> selectByParam(@RequestBody Map<String, BaseQueryValue> queryPrams) {
        List<IdModel> result = getBaseService().selectListByParam(queryPrams);
        db2pageList(result);
        return Result.success(result);
    }

    /**
     * 根据查询条件返回所有符合查询结果的数据，并转换
     *
     * @param pageSearchParam
     * @return
     */
    @AspectLog(description = "分页查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectPage")
    @ResponseBody
    default Result<IPage<IdModel>> selectPage(@RequestBody PageSearchParam pageSearchParam) {
        Result<IPage<IdModel>> result = getBaseService().selectPage(pageSearchParam);
        db2pageList(result.getResult().getRecords());
        return result;
    }

    /**
     * 根据查询条件及数据权限过滤返回所有符合查询结果的数据，并转换。
     *
     * @param pageSearchParam
     * @return
     */
    @AspectLog(description = "分页查询实体（数据权限过滤）", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectPageByDataFilter")
    @ResponseBody
    default Result<IPage<IdModel>> selectPageByDataFilter(@RequestBody PageSearchParam pageSearchParam, HttpServletRequest request) {
        Result<IPage<IdModel>> result = getBaseService().selectPageByDataFilter(pageSearchParam, request, ConstantUser.getSystemParam());
        db2pageList(result.getResult().getRecords());
        return result;
    }

}

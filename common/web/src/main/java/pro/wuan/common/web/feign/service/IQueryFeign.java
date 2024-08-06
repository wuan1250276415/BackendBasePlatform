package pro.wuan.common.web.feign.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.core.utils.ConvertUtil;
import pro.wuan.common.db.util.TableUtil;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;

import java.util.*;

public interface IQueryFeign<Service extends IBaseService, Mapper extends IBaseMapper, IdModel extends IDModel> extends IBaseFeignService<Mapper, IdModel> {
    /**
     * 根据ID查询信息，根据实体注解进行转换
     *
     * @param id
     * @return
     */
    @AspectLog(description = "根据ID查询实体", type = OperationType.OPERATION_QUERY)
    @GetMapping("/selectById")
    @ResponseBody
    default IdModel selectById(@RequestParam("id") Long id) {
        IdModel idModel = getBaseService().selectById(id);
        db2page(idModel);
        return idModel;
    }

    /**
     * 根据ID集合批量查询，根据实体注解进行转换
     *
     * @param ids
     * @return
     */
    @AspectLog(description = "根据ID查询实体", type = OperationType.OPERATION_QUERY)
    @GetMapping("/selectBatchByIds")
    @ResponseBody
    default List<IdModel> selectBatchByIds(@RequestParam("ids") List<Long> ids) {
        List<IdModel> idModels = getBaseService().selectBatchIds(ids);
        db2pageList(idModels);
        return idModels;
    }

    /**
     * 根据实体属性查询实体信息
     *
     * @param idModel idModel
     * @return
     */
    @AspectLog(description = "根据实体属性精确查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping("/selectByProperty")
    @ResponseBody
    default List<IdModel> selectByProperty(@RequestParam(value = "idModel") IdModel idModel) throws InstantiationException, IllegalAccessException {
        Map<String, Object> map = ConvertUtil.objectToMap(idModel);
        Map<String, Object> params = new HashMap<String, Object>();
        for (String key : map.keySet()) {
            String realColumn = TableUtil.property2column(key.toUpperCase(Locale.ROOT), getModelClass());
            if (StringUtils.isNotEmpty(realColumn)) {
                params.put(realColumn, map.get(key));
            }
        }
        if (params.size() == 0) {
            return new ArrayList<IdModel>();
        }
        QueryWrapper<IdModel> queryWrapper = new QueryWrapper<IdModel>();
        queryWrapper.allEq(params);
        List<IdModel> idModels = getBaseService().selectList(queryWrapper);
        db2pageList(idModels);
        return idModels;
    }

    /**
     * 根据查询条件返回所有符合查询结果的数据，并转换
     *
     * @param pageSearchParam pageSearchParam
     * @return
     */
    @AspectLog(description = "分页查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectPage")
    @ResponseBody
    default IPage<IdModel> selectPage(@RequestParam(value = "pageSearchParam") PageSearchParam pageSearchParam) {
        Result<IPage<IdModel>> result = getBaseService().selectPage(pageSearchParam);
        db2pageList(result.getResult().getRecords());
        return result.getResult();
    }
}

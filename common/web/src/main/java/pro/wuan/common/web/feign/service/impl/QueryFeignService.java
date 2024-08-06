package pro.wuan.common.web.feign.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.feign.service.IQueryFeign;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @program: tellhowcloud
 * @description: 通用查询微服务接口
 * @author: HawkWang
 * @create: 2021-08-27 11:39
 **/
public abstract class QueryFeignService<Service extends IBaseService, Mapper extends IBaseMapper, IdModel extends IDModel> implements IQueryFeign<Service, Mapper, IdModel> {

    @Autowired
    private Service service;

    Class<IdModel> modelClass = null;

    @Override
    public IBaseService getBaseService() {
        return service;
    }

    @Override
    public Class<IdModel> getModelClass() {
        if (modelClass == null) {
            this.modelClass = (Class<IdModel>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];
        }
        return this.modelClass;
    }

    /**
     * 通用微服务接口-根据ID查询信息，根据实体注解进行转换
     *
     * @param id
     * @return
     */
    @AspectLog(description = "根据ID查询实体", type = OperationType.OPERATION_QUERY)
    @GetMapping("/selectById")
    @ResponseBody
    @Override
    public IdModel selectById(@RequestParam("id") Long id) {
        return IQueryFeign.super.selectById(id);
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
    @Override
    public List<IdModel> selectBatchByIds(@RequestParam("ids") List<Long> ids) {
        return IQueryFeign.super.selectBatchByIds(ids);
    }

    /**
     * 通用微服务接口-根据实体属性查询实体信息
     *
     * @param idModel idModel
     * @return
     */
    @AspectLog(description = "根据实体属性精确查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping("/selectByProperty")
    @ResponseBody
    @Override
    public List<IdModel> selectByProperty(@RequestParam("idModel") IdModel idModel) throws InstantiationException, IllegalAccessException {
        return IQueryFeign.super.selectByProperty(idModel);
    }

    /**
     * 通用微服务接口-根据查询条件返回所有符合查询结果的数据，并转换
     *
     * @param pageSearchParam pageSearchParam
     * @return
     */
    @AspectLog(description = "分页查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectPage")
    @ResponseBody
    @Override
    public IPage<IdModel> selectPage(@RequestParam("pageSearchParam") PageSearchParam pageSearchParam) {
        return IQueryFeign.super.selectPage(pageSearchParam);
    }
}

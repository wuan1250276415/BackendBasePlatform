package pro.wuan.common.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.core.annotation.constraints.group.ValidatorQueryCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.core.utils.ReflectionUtils;
import pro.wuan.common.core.utils.SpringBeanUtil;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.service.IColumnFilterService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @program: tellhowcloud
 * @description: CURD控制器超类
 * @author: HawkWang
 * @create: 2021-08-25 14:12
 **/
@Slf4j
public abstract class CURDController<Service extends IBaseService, Mapper extends IBaseMapper, IdModel extends IDModel> implements SaveController<Service, Mapper, IdModel>, DeleteController<Service, Mapper, IdModel>, QueryController<Service, Mapper, IdModel>, UpdateController<Service, Mapper, IdModel>, QueryAuthController<Service, Mapper, IdModel> {

    Class<IdModel> modelClass = null;

    @Resource
    protected Service service;

    @Override
    public Class getModelClass() {
        if (modelClass == null) {
            this.modelClass = (Class<IdModel>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];
        }
        return this.modelClass;
    }

    @Override
    public IBaseService getBaseService() {
        return service;
    }

    private IColumnFilterService columnFilterService;

    /**
     * 获取数据权限的Service
     *
     * @return
     */
    @Override
    public IColumnFilterService getColumnFilterService() {
        if(null == columnFilterService){
            columnFilterService = SpringBeanUtil.getBeanIgnoreEx(IColumnFilterService.class);
        }
        return columnFilterService;
    }

    /**
     * 通用接口-根据ID删除实体
     *
     * @param id 实体的ID
     * @return 删除成功/失败
     */
    @AspectLog(description = "根据ID删除实体", type = OperationType.OPERATION_DELETE)
    @GetMapping(value = "/delete/{id}")
    @ResponseBody
    @Override
    public Result<String> delete(@PathVariable("id") Long id) {
        return DeleteController.super.delete(id);
    }

    /**
     * 通用接口-批量删除用户信息
     *
     * @param ids 逗号分割的id字符串
     * @return 失败或者成功的结果
     */
    @AspectLog(description = "根据Ids批量删除实体", type = OperationType.OPERATION_DELETE)
    @GetMapping("/deleteBatchIds")
    @ResponseBody
    @Override
    public Result<String> deleteBatchIds(@NotNull String ids) {
        return DeleteController.super.deleteBatchIds(ids);
    }

    /**
     * 通用接口-根据ID查询信息，根据实体注解进行转换
     *
     * @param id
     * @return
     */
    @AspectLog(description = "根据ID查询实体", type = OperationType.OPERATION_QUERY)
    @GetMapping("/selectById/{id}")
    @ResponseBody
    @Override
    public Result<IdModel> selectById(@PathVariable("id") Long id) {
        return QueryController.super.selectById(id);
    }

    /**
     * 根据id集合获取列表
     *
     * @param ids
     * @return
     */
    @AspectLog(description = "根据id集合获取列表", type = OperationType.OPERATION_QUERY)
    @GetMapping("/selectBatchByIds")
    @ResponseBody
    @Override
    public Result<List<IdModel>> selectBatchByIds(@RequestParam("ids") List<Long> ids) {
        return QueryController.super.selectBatchByIds(ids);
    }

    /**
     * 通用接口-根据实体属性查询实体信息，实体至少有一个属性有不为空
     *
     * @param idModel 实体对象
     * @return
     */
    @AspectLog(description = "根据实体属性查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping("/selectByProperty")
    @ResponseBody
    @Override
    public Result<List<IdModel>> selectByProperty(@RequestBody @Validated(value = ValidatorQueryCheck.class) IdModel idModel) {
        boolean hasProperty = false;
        Class idModelClass = idModel.getClass();
        Field[] fields = idModelClass.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            try {
                Object obj = ReflectionUtils.getFieldValue(idModel, field.getName());
                if (null != obj) {
                    hasProperty = true;
                    break;
                }
            } catch (Exception ex) {
                log.warn("获取对象的属性值{}出现异常。", field.getName());
                //忽略此处的异常
            }
        }
        //必须有一个属性有值，不能全部都为空
        if (!hasProperty) {
            log.warn("当前实体属性全部为空，不允许查询。");
            return Result.failure("属性不可以全为空");
        }
        return QueryController.super.selectByProperty(idModel);
    }

    /**
     * 通用接口-根据查询条件返回所有符合查询结果的数据，并转换。
     *
     * @param pageSearchParam
     * @return
     */
    @AspectLog(description = "分页查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectPage")
    @ResponseBody
    @Override
    public Result<IPage<IdModel>> selectPage(@RequestBody @Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam) {
        return QueryController.super.selectPage(pageSearchParam);
    }

    /**
     * 通用接口-根据查询条件及数据权限返回所有符合查询结果的数据，并转换。
     *
     * @param pageSearchParam
     * @param request
     * @return
     */
    @AspectLog(description = "分页查询实体（数据权限）", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectPageByDataFilter")
    @ResponseBody
    @Override
    public Result<IPage<IdModel>> selectPageByDataFilter(@RequestBody @Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam,
                                                         HttpServletRequest request) {
        return QueryController.super.selectPageByDataFilter(pageSearchParam, request);
    }

    /**
     * 通用接口-根据查询条件及数据权限过滤返回所有符合查询结果的数据，依据列表数据权限过滤字段，并转换。
     *
     * @param pageSearchParam
     * @param request
     * @return
     */
    @AspectLog(description = "分页查询实体（数据权限过滤，列表权限过滤）", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectPageByColumnWithDataFilter")
    @ResponseBody
    @Override
    public Result<IPage<IdModel>> selectPageByColumnWithDataFilter(PageSearchParam pageSearchParam, HttpServletRequest request) {
        return QueryAuthController.super.selectPageByColumnWithDataFilter(pageSearchParam, request);
    }

    /**
     * 通用接口-通用的保存方法
     *
     * @param idModel 泛型实体对象 根据Controller实现类确定泛型
     * @return
     */
    @AspectLog(description = "新增实体", type = OperationType.OPERATION_ADD)
    @PostMapping(value = "/save")
    @ResponseBody
    @Override
    public Result<IdModel> save(@RequestBody @Validated(value = ValidatorSaveCheck.class) IdModel idModel) {
        return SaveController.super.save(idModel);
    }

    /**
     * 通用接口-修改部分字段
     *
     * @param idModel
     * @return
     */
    @AspectLog(description = "根据ID更新实体有值属性", type = OperationType.OPERATION_UPDATE)
    @PostMapping(value = "/updateById")
    @ResponseBody
    @Override
    public Result<IdModel> update(@RequestBody @Validated(value = ValidatorUpdateCheck.class) IdModel idModel) {
        return UpdateController.super.update(idModel);
    }

    /**
     * 通用接口-修改所有字段
     *
     * @param idModel 实体
     * @return
     */
    @AspectLog(description = "根据ID更新实体全部属性", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateAllById")
    @ResponseBody
    @Override
    public Result<IdModel> updateAll(@RequestBody @Validated(value = ValidatorUpdateCheck.class) IdModel idModel) {
        return UpdateController.super.updateAll(idModel);
    }
}

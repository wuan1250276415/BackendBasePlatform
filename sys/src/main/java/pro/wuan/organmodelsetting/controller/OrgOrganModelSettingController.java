package pro.wuan.organmodelsetting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.core.annotation.constraints.group.ValidatorQueryCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.RedisConstant;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.redis.util.JedisUtil;
import pro.wuan.common.web.controller.EmptyController;
import pro.wuan.organmodelsetting.mapper.OrgOrganModelSettingMapper;
import pro.wuan.organmodelsetting.model.OrgOrganModelSettingModel;
import pro.wuan.organmodelsetting.service.IOrgOrganModelSettingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 查询模式数据配置
 * @program: tellhowcloud
 * @author libra
 * @create 2024-01-22 11:48:48
 */
@RestController
@RequestMapping("/OrgOrganModelSetting")
public class OrgOrganModelSettingController extends EmptyController<IOrgOrganModelSettingService, OrgOrganModelSettingMapper, OrgOrganModelSettingModel> {
    @Autowired
    private JedisUtil jedisUtil;
    /**
     * 查询模式数据配置-根据ID删除实体
     * @author libra
     * @param id 实体的ID
     * @return 删除成功/失败
     */
    @AspectLog(description = "根据ID删除实体", type = OperationType.OPERATION_DELETE)
    @GetMapping(value = "/delete/{id}")
    @ResponseBody
    public Result<String> delete(@PathVariable("id") Long id) {
        if (service.deleteById(id) > 0) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

    /**
     * 查询模式数据配置-批量删除用户信息
     * @author libra
     * @param ids 逗号分割的id字符串
     * @return 失败或者成功的结果
     */
    @AspectLog(description = "根据Ids批量删除实体", type = OperationType.OPERATION_DELETE)
    @GetMapping("/deleteBatchIds")
    @ResponseBody
    public Result<String> deleteBatchIds(String ids) {
        Assert.notNull(ids, "请传入参数！");
        List _ids = new ArrayList<>(Arrays.asList(ids.split(",")));
        if (service.deleteBatchIds(_ids) > 0) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

    /**
     * 查询模式数据配置-根据ID查询信息，根据实体注解进行转换
     * @author libra
     * @param id
     * @return
     */
    @AspectLog(description = "根据ID查询实体", type = OperationType.OPERATION_QUERY)
    @GetMapping("/selectById/{id}")
    @ResponseBody
    public Result<OrgOrganModelSettingModel> selectById(@PathVariable Long id) {
        OrgOrganModelSettingModel model = service.selectById(id);
        db2page(model);
        return Result.success(model);
    }

    /**
     * 查询模式数据配置-根据查询条件返回所有符合查询结果的数据，并转换。
     * @author libra
     * @param pageSearchParam
     * @return
     */
    @AspectLog(description = "分页查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectPage")
    @ResponseBody
    public Result<IPage<OrgOrganModelSettingModel>> selectPage(@RequestBody @Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam) {
        Result<IPage<OrgOrganModelSettingModel>> result = service.selectPage(pageSearchParam);
        db2pageList(result.getResult().getRecords());
        return result;
    }

    /**
     * 查询模式数据配置-通用的保存方法
     * @author libra
     * @param model 泛型实体对象 根据Controller实现类确定泛型
     * @return
     */
    @AspectLog(description = "新增实体", type = OperationType.OPERATION_ADD)
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<OrgOrganModelSettingModel> save(@RequestBody @Validated(value = ValidatorSaveCheck.class) OrgOrganModelSettingModel model) {
        if (service.insert(model) > 0) {
            return Result.success(model);
        } else {
            return Result.failure(model);
        }
    }

    /**
     * 查询模式数据配置-修改部分字段
     * @author libra
     * @param model
     * @return
     */
    @AspectLog(description = "根据ID更新实体有值属性", type = OperationType.OPERATION_UPDATE)
    @PostMapping(value = "/updateById")
    @ResponseBody
    public Result<OrgOrganModelSettingModel> update(@RequestBody @Validated(value = ValidatorUpdateCheck.class) OrgOrganModelSettingModel model) {
        if (service.updateById(model) == 1) {
            jedisUtil.delete(RedisConstant.SUB_ORGAN_CHANGE_KEY);
            return Result.success(model);
        } else {
            return Result.failure(model);
        }
    }

}

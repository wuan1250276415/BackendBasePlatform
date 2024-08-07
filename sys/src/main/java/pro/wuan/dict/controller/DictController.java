package pro.wuan.dict.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.BaseSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.utils.ConvertUtil;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.dict.mapper.SysDictionaryMapper;
import pro.wuan.dict.service.ISysDictionaryService;
import pro.wuan.feignapi.dictapi.constant.DictSaveCheck;
import pro.wuan.feignapi.dictapi.entity.SysDictionary;
import pro.wuan.feignapi.dictapi.model.DictParams;

import java.util.List;

/**
 * 数据字典
 *
 * @author: oldone
 * @date: 2021/9/7 16:51
 */
@Slf4j
@RestController
@RequestMapping("/dict")
public class DictController extends CURDController<ISysDictionaryService, SysDictionaryMapper, SysDictionary> {

    /**
     * 新增字典类型
     *
     * @param sysDictionary 数据字典实体
     * @return
     */
    @AspectLog(description = "新增字典类型", type = OperationType.OPERATION_ADD)
    @PostMapping(value = "/addDictType")
    @ResponseBody
    public Result<SysDictionary> addDictType(@RequestBody @Validated(value = ValidatorSaveCheck.class) SysDictionary sysDictionary) {
        if (sysDictionary.getParentId() == null || CommonConstant.DEFAULT_PARENT_ID.equals(sysDictionary.getParentId())) {
            sysDictionary.setParentId(CommonConstant.DEFAULT_PARENT_ID);
        } else {
            if (sysDictionary.getCode() == null) {
                return Result.failure("编码值不能为空");
            }
            if (service.isExistsByCode(sysDictionary.getId(), sysDictionary.getCode())) {
                return Result.failure("编码已存在");
            }
        }

        if (service.addOrUpdate(sysDictionary)) {
            return Result.success(sysDictionary);
        } else {
            return Result.failure(sysDictionary);
        }
    }

    /**
     * 修改字典类型
     *
     * @param sysDictionary 数据字典实体
     * @return
     */
    @AspectLog(description = "修改字典类型", type = OperationType.OPERATION_UPDATE)
    @PostMapping(value = "/updateDictType")
    @ResponseBody
    public Result<SysDictionary> updateDictType(@RequestBody @Validated(value = ValidatorUpdateCheck.class) SysDictionary sysDictionary) {
        if (sysDictionary.getParentId() == null || CommonConstant.DEFAULT_PARENT_ID.equals(sysDictionary.getParentId())) {
            sysDictionary.setParentId(CommonConstant.DEFAULT_PARENT_ID);
        } else {
            if (sysDictionary.getCode() == null) {
                return Result.failure("编码不能为空");
            }
            if (service.isExistsByCode(sysDictionary.getId(), sysDictionary.getCode())) {
                return Result.failure("编码已存在");
            }
        }

        if (service.addOrUpdate(sysDictionary)) {
            return Result.success(sysDictionary);
        } else {
            return Result.failure(sysDictionary);
        }
    }

    /**
     * 新增数据字典
     *
     * @param sysDictionary 数据字典实体
     * @return
     */
    @AspectLog(description = "新增实体", type = OperationType.OPERATION_ADD)
    @PostMapping(value = "/addDict")
    @ResponseBody
    public Result<SysDictionary> addDict(@RequestBody @Validated(value = DictSaveCheck.class) SysDictionary sysDictionary) {
        //判断该字典项下是否已存在重复的value值
        if (service.isExistsByValue(sysDictionary.getId(), sysDictionary.getCode(), sysDictionary.getValue())) {
            return Result.failure("该字典项已存在重复的字典值");
        }
        if (service.addOrUpdate(sysDictionary)) {
            return Result.success(sysDictionary);
        } else {
            return Result.failure(sysDictionary);
        }
    }

    /**
     * 修改数据字典
     *
     * @param sysDictionary 数据字典实体
     * @return
     */
    @AspectLog(description = "新增实体", type = OperationType.OPERATION_UPDATE)
    @PostMapping(value = "/updateDict")
    @ResponseBody
    public Result<SysDictionary> updateDict(@RequestBody @Validated(value = DictSaveCheck.class) SysDictionary sysDictionary) {
        //判断该字典项下是否已存在重复的value值
        if (service.isExistsByValue(sysDictionary.getId(), sysDictionary.getCode(), sysDictionary.getValue())) {
            return Result.failure("该字典项已存在重复的字典值");
        }
        if (service.addOrUpdate(sysDictionary)) {
            return Result.success(sysDictionary);
        } else {
            return Result.failure(sysDictionary);
        }
    }

    /**
     * 根据id删除字典或字典项
     *
     * @param id 字典id
     * @return 删除成功/失败
     */
    @Override
    public Result<String> delete(@PathVariable("id") Long id) {
        if (service.hasChildren(id)) {
            return Result.failure("存在子项，无法删除！");
        }
        if (service.deleteDictById(id)) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

    /**
     * 批量删除字典
     *
     * @param ids 逗号分割的id字符串
     * @return
     */
    @Override
    public Result<String> deleteBatchIds(String ids) {
        Assert.notNull(ids, "请传入参数！");
        if (service.hasChildrenByIds(ids)) {
            return Result.failure("存在子项，无法删除！");
        }
        if (service.deleteDictBatchIds(ids)) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

    /**
     * 根据id获取字典
     *
     * @param id 字典id
     * @return
     */
    @Override
    public Result<SysDictionary> selectById(@PathVariable Long id) {
        if (id == null) {
            Result.failure("字典id不能为空");
        }
        try {
            return Result.success(service.getById(id));
        } catch (Exception e) {
            log.error("dict getByParentCode error:" + e);
            return Result.failure("获取信息异常");
        }
    }


    /**
     * 获取应用对应的字典类型
     *
     * @return
     */
    @AspectLog(description = "获取应用对应的字典类型", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getDictTypesByAppId")
    @ResponseBody
    public Result getDictTypesByAppId(@RequestParam("appId") Long appId) {
        if (appId == null) {
            return Result.failure("请先选择所属应用");
        }
        try {
            return Result.success(service.getByAppId(appId, true));
        } catch (Exception e) {
            log.error("getDictTypesByAppId error:", e);
            return Result.failure("获取数据失败");
        }
    }


    /**
     * 获取字典类型树
     *
     * @return
     */
    @AspectLog(description = "获取字典类型树", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getTypeTree")
    @ResponseBody
    public Result getTypeTree(@RequestParam("appId") Long appId) {
        if (appId == null) {
            return Result.failure("请先选择所属应用");
        }
        try {
            List<SysDictionary> dicts = service.getByAppId(appId, false);
            SysDictionary root = this.getRootNode();
            root.setDictItems(dicts);
            return Result.success(root);
        } catch (Exception e) {
            log.error("getDictTypesByAppId error:", e);
            return Result.failure("获取数据失败");
        }
    }

    /**
     * 获取字典名称树
     *
     * @return
     */
    @AspectLog(description = "获取应用对应的字典类型", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getDictTree")
    @ResponseBody
    public Result getDictTree(@RequestParam("parentId") Long parentId) {
        if (parentId == null) {
            return Result.failure("父节点不能为空");
        }
        try {
            DictParams dictParams = new DictParams();
            dictParams.setParentId(parentId);
            List<SysDictionary> dicts = service.getByParentId(dictParams);
            SysDictionary root = this.getRootNode();
            root.setDictItems(dicts);
            return Result.success(root);
        } catch (Exception e) {
            log.error("getDictTree error:", e);
            return Result.failure("获取数据失败");
        }
    }


    /**
     * 数据字典分页查询
     *
     * @param searchParam 数据字典分页查询
     * @return 字典项集合
     */
    @AspectLog(description = "根据父节点编码获取字典项集合", type = OperationType.OPERATION_QUERY)
    @PostMapping("/getPageList")
    @ResponseBody
    public Result getPageList(@RequestBody BaseSearchParam searchParam) {
        if (searchParam == null || searchParam.getQueryPrams() == null) {
            return Result.failure("父编码不能为空");
        }
        try {
            DictParams dictParams = ConvertUtil.queryMapToObject(searchParam.getQueryPrams(), DictParams.class);
            if (dictParams == null || dictParams.getParentId() == null) {
                return Result.failure("父编码不能为空");
            }
            dictParams.setSortField(searchParam.getSortField());
            dictParams.setSortOrder(searchParam.getSortOrder());
            return Result.success(service.getByParentId(dictParams));
        } catch (Exception e) {
            log.error("dict getPageList error:" + e);
            return Result.failure("获取信息异常");
        }
    }


    /**
     * 数据字典控件绑定
     *
     * @param code 字典code
     * @return 字典项集合
     */
    @AspectLog(description = "数据字典控件绑定", type = OperationType.OPERATION_QUERY)
    @GetMapping("/bindDict")
    @ResponseBody
    public Result bindDict(@RequestParam("code") String code) {
        if (code == null) {
            return Result.failure("字典编码不能为空");
        }
        try {
            return Result.success(service.bindDict(code));
        } catch (Exception e) {
            log.error("dict bindDict error:" + e);
            return Result.failure("获取信息异常");
        }
    }


    /**
     * 树形字典控件绑定
     *
     * @param parentId 父节点id
     * @return 字典项集合
     */
    @AspectLog(description = "树形字典控件绑定", type = OperationType.OPERATION_QUERY)
    @GetMapping("/bindDictByParentId")
    @ResponseBody
    public Result bindDictByParentId(@RequestParam("parentId") Long parentId) {
        if (parentId == null) {
            return Result.failure("父节点不能为空");
        }
        try {
            return Result.success(service.bindDictByParentId(parentId));
        } catch (Exception e) {
            log.error("dict bindDict error:" + e);
            return Result.failure("获取信息异常");
        }
    }

    private SysDictionary getRootNode() {
        SysDictionary root = new SysDictionary();
        root.setName("顶级节点");
        root.setId(CommonConstant.DEFAULT_PARENT_ID);
        return root;
    }

    /**
     * 刷新缓存
     *
     * @return
     */
    @GetMapping("/refreashCache")
    @ResponseBody
    public Result refreashCache() {
        //刷新缓存
        service.refreashCache();
        return Result.success();
    }

}

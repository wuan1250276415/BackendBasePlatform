package pro.wuan.privilege.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.core.annotation.constraints.group.ValidatorQueryCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.convertor.db2page.DbConvert2Page;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeColumnModel;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeColumnSymbolModel;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeModel;
import pro.wuan.privilege.mapper.OrgDataSchemeMapper;
import pro.wuan.privilege.service.IOrgDataSchemeColumnService;
import pro.wuan.privilege.service.IOrgDataSchemeColumnSymbolService;
import pro.wuan.privilege.service.IOrgDataSchemeService;
import pro.wuan.privilege.vo.ConditionGroup;
import pro.wuan.privilege.vo.DataSchemeColumnMap;
import pro.wuan.privilege.vo.OrgDataSchemeVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据方案
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@RestController
@RequestMapping("/org/dataScheme")
public class OrgDataSchemeController extends CURDController<IOrgDataSchemeService, OrgDataSchemeMapper, OrgDataSchemeModel> {

    @Autowired
    private IOrgDataSchemeColumnService dataSchemeColumnService;

    @Autowired
    private IOrgDataSchemeColumnSymbolService dataSchemeColumnSymbolService;

    /**
     * 分页查询数据方案字段
     *
     * @param pageSearchParam 分页查询内容
     * @return 数据方案字段实体分页列表
     */
    @AspectLog(description = "分页查询数据方案字段", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectDataSchemeColumnPage")
    public Result<IPage<OrgDataSchemeColumnModel>> selectDataSchemeColumnPage(@RequestBody @Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam) {
        try {
            return service.selectDataSchemeColumnPage(pageSearchParam);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 根据ID查询数据方案字段
     *
     * @param id 数据方案字段id
     * @return 数据方案字段实体
     */
    @AspectLog(description = "根据ID查询数据方案字段", type = OperationType.OPERATION_QUERY)
    @GetMapping("/selectDataSchemeColumnById")
    public Result<OrgDataSchemeColumnModel> selectDataSchemeColumnById(Long id) {
        try {
            Assert.notNull(id, "id不能为空");
            OrgDataSchemeColumnModel columnModel = dataSchemeColumnService.selectById(id);
            Assert.notNull(columnModel, "数据方案字段信息不存在");
            List<OrgDataSchemeColumnSymbolModel> symbolModelList = dataSchemeColumnSymbolService.getDataSchemeColumnSymbolByColumn(id);
            if (CollectionUtil.isNotEmpty(symbolModelList)) {
                List<String> conditionSymbolList = new ArrayList<>();
                List<String> conditionSymbolNameList = new ArrayList<>();
                CommonConstant.COLUMN_CONDITION_SYMBOL.values();
                for (OrgDataSchemeColumnSymbolModel symbolModel : symbolModelList) {
                    conditionSymbolList.add(symbolModel.getConditionSymbol());
                    conditionSymbolNameList.add(symbolModel.getConditionSymbolName());
                }
                columnModel.setConditionSymbolList(conditionSymbolList);
                columnModel.setConditionSymbolNameList(conditionSymbolNameList);
            }
            DbConvert2Page dbConvert2Page = new DbConvert2Page();
            dbConvert2Page.executeDbToPage4Model(columnModel);
            return Result.success(columnModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 保存数据方案字段
     *
     * @param columnModel 数据方案字段实体
     * @return 数据方案字段实体
     */
    @AspectLog(description = "保存数据方案字段", type = OperationType.OPERATION_ADD)
    @PostMapping("/saveDataSchemeColumn")
    public Result<OrgDataSchemeColumnModel> saveDataSchemeColumn(@RequestBody @Validated(value = ValidatorSaveCheck.class) OrgDataSchemeColumnModel columnModel) {
        try {
            return dataSchemeColumnService.saveDataSchemeColumn(columnModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 更新数据方案字段
     *
     * @param columnModel 数据方案字段实体
     * @return 数据方案字段实体
     */
    @AspectLog(description = "更新数据方案字段", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateDataSchemeColumn")
    public Result<OrgDataSchemeColumnModel> updateDataSchemeColumn(@RequestBody @Validated(value = ValidatorUpdateCheck.class) OrgDataSchemeColumnModel columnModel) {
        try {
            return dataSchemeColumnService.updateDataSchemeColumn(columnModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 删除数据方案字段
     *
     * @param id 数据方案字段id
     * @return 删除成功/失败
     */
    @AspectLog(description = "删除数据方案字段", type = OperationType.OPERATION_DELETE)
    @GetMapping("/deleteDataSchemeColumn")
    public Result<String> deleteDataSchemeColumn(Long id) {
        try {
            return dataSchemeColumnService.deleteDataSchemeColumn(id);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 编辑数据方案时获取数据方案字段信息集合
     *
     * @param menuResourceId 菜单资源id
     * @return 数据方案字段信息集合
     */
    @AspectLog(description = "获取数据方案字段信息集合", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getDataSchemeColumnMap")
        public Result<DataSchemeColumnMap> getDataSchemeColumnMap(Long menuResourceId) {
        try {
            return Result.success(dataSchemeColumnService.getDataSchemeColumnMap(menuResourceId));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 根据ID查询数据方案
     *
     * @param id 数据方案id
     * @return 数据方案实体
     */
    @AspectLog(description = "根据ID查询数据方案", type = OperationType.OPERATION_QUERY)
    @GetMapping("/selectDataSchemeById")
    public Result<OrgDataSchemeVO> selectDataSchemeById(Long id) {
        try {
            Assert.notNull(id, "id不能为空");
            OrgDataSchemeModel schemeModel = service.selectById(id);
            Assert.notNull(schemeModel, "数据方案信息不存在");
            OrgDataSchemeVO schemeVO = new OrgDataSchemeVO();
            BeanUtils.copyProperties(schemeModel, schemeVO);
            List<ConditionGroup> conditionGroupList = JSONObject.parseObject(schemeVO.getConditionJson(), new TypeReference<>() {
            });
            schemeVO.setConditionGroupList(conditionGroupList);
            return Result.success(schemeVO);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 保存数据方案
     *
     * @param schemeVO 数据方案VO
     * @return 数据方案实体
     */
    @AspectLog(description = "保存数据方案", type = OperationType.OPERATION_ADD)
    @PostMapping("/saveDataScheme")
    public Result<OrgDataSchemeModel> saveDataScheme(@RequestBody @Validated(value = ValidatorSaveCheck.class) OrgDataSchemeVO schemeVO) {
        try {
            return service.saveDataScheme(schemeVO);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 更新数据方案
     *
     * @param schemeVO 数据方案VO
     * @return 数据方案实体
     */
    @AspectLog(description = "更新数据方案", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updateDataScheme")
    public Result<OrgDataSchemeModel> updateDataScheme(@RequestBody @Validated(value = ValidatorUpdateCheck.class) OrgDataSchemeVO schemeVO) {
        try {
            return service.updateDataScheme(schemeVO);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 删除数据方案
     *
     * @param id 数据方案id
     * @return 删除成功/失败
     */
    @AspectLog(description = "删除数据方案", type = OperationType.OPERATION_DELETE)
    @GetMapping("/deleteDataScheme")
    public Result<String> deleteDataScheme(Long id) {
        try {
            return service.deleteDataScheme(id);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

}

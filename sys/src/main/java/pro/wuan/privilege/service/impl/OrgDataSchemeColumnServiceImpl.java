package pro.wuan.privilege.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.OrgBusinessResourceModel;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeColumnModel;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeColumnSymbolModel;
import pro.wuan.privilege.mapper.OrgBusinessResourceMapper;
import pro.wuan.privilege.mapper.OrgDataSchemeColumnMapper;
import pro.wuan.privilege.service.IOrgDataSchemeColumnService;
import pro.wuan.privilege.service.IOrgDataSchemeColumnSymbolService;
import pro.wuan.privilege.vo.DataSchemeColumnAndSymbolIdVO;
import pro.wuan.privilege.vo.DataSchemeColumnAndSymbolVO;
import pro.wuan.privilege.vo.DataSchemeColumnDetailVO;
import pro.wuan.privilege.vo.DataSchemeColumnMap;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * 数据方案字段
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Validated
@Service("orgDataSchemeColumnService")
public class OrgDataSchemeColumnServiceImpl extends BaseServiceImpl<OrgDataSchemeColumnMapper, OrgDataSchemeColumnModel> implements IOrgDataSchemeColumnService {

    @Autowired
    private IOrgDataSchemeColumnSymbolService dataSchemeColumnSymbolService;

    @Resource
    private OrgBusinessResourceMapper businessResourceMapper;

    /**
     * 保存数据方案字段
     *
     * @param columnModel 数据方案字段实体
     * @return 数据方案字段实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgDataSchemeColumnModel> saveDataSchemeColumn(OrgDataSchemeColumnModel columnModel) {
        this.checkWhenEditDataSchemeColumn(columnModel);
        // 保存数据方案字段
        if (this.insert(columnModel) <= 0) {
            return Result.failure(columnModel);
        }
        // 保存数据方案字段、条件符号关联表
        this.batchSaveDataSchemeColumnSymbol(columnModel);
        return Result.success(columnModel);
    }

    /**
     * 更新数据方案字段
     *
     * @param columnModel 数据方案字段实体
     * @return 数据方案字段实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgDataSchemeColumnModel> updateDataSchemeColumn(OrgDataSchemeColumnModel columnModel) {
        this.checkWhenEditDataSchemeColumn(columnModel);
        // 更新数据方案字段
        if (this.updateById(columnModel) < 1) {
            return Result.failure(columnModel);
        }
        // 删除原数据方案字段、条件符号关联表
        dataSchemeColumnSymbolService.batchDeleteDataSchemeColumnSymbol(columnModel.getId());
        // 保存新数据方案字段、条件符号关联表
        this.batchSaveDataSchemeColumnSymbol(columnModel);
        return Result.success(columnModel);
    }

    /**
     * 编辑数据方案字段时数据校验
     *
     * @param columnModel
     * @return void
     */
    private void checkWhenEditDataSchemeColumn(OrgDataSchemeColumnModel columnModel) {
        Assert.notNull(columnModel.getMenuResourceId(), "菜单资源id不能为空");
        OrgBusinessResourceModel menu = businessResourceMapper.selectById(columnModel.getMenuResourceId());
        Assert.notNull(menu, "菜单资源不存在");
        Assert.notNull(menu.getAppId(), "菜单资源关联的应用id不能为空");
        Assert.isTrue(CommonConstant.BUSINESS_RESOURCE_TYPE.MENU.getValue().equals(menu.getType()), "只能关联菜单资源");
        Assert.isTrue(!this.isExistColumnName(columnModel.getId(), columnModel.getMenuResourceId(), columnModel.getColumnName()), "字段名称已经存在，请重新输入");
    }

    /**
     * 字段名称是否存在
     *
     * @param id id
     * @param menuResourceId 菜单资源id
     * @param columnName 字段名称
     * @return true/false
     */
    private boolean isExistColumnName(Long id, Long menuResourceId, String columnName) {
        LambdaQueryWrapper<OrgDataSchemeColumnModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgDataSchemeColumnModel::getMenuResourceId, menuResourceId);
        wrapper.eq(OrgDataSchemeColumnModel::getColumnName, columnName);
        if (id != null) {
            wrapper.ne(OrgDataSchemeColumnModel::getId, id);
        }
        Long count = dao.selectCount(wrapper);
        return (count != null && count > 0) ? true : false;
    }

    /**
     * 保存数据方案字段、条件符号关联表
     *
     * @param columnModel
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveDataSchemeColumnSymbol(OrgDataSchemeColumnModel columnModel) {
        List<OrgDataSchemeColumnSymbolModel> symbolModelList = new ArrayList<>();
        List<String> conditionSymbolList = columnModel.getConditionSymbolList();
        if (CollectionUtil.isEmpty(conditionSymbolList)) {
            return;
        }
        for (String conditionSymbol : conditionSymbolList) {
            OrgDataSchemeColumnSymbolModel symbolModel = new OrgDataSchemeColumnSymbolModel();
            symbolModel.setDataSchemeColumnId(columnModel.getId());
            symbolModel.setConditionSymbol(conditionSymbol);
            symbolModelList.add(symbolModel);
        }
        dataSchemeColumnSymbolService.batchInsertNoCascade(symbolModelList);
    }

    /**
     * 删除数据方案字段
     *
     * @param id 数据方案字段id
     * @return 删除成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteDataSchemeColumn(@NotNull(message = "id不能为空") Long id) {
        OrgDataSchemeColumnModel columnModel = this.selectById(id);
        Assert.notNull(columnModel, "数据方案字段信息不存在");
        // 删除原数据方案字段、条件符号关联表
        dataSchemeColumnSymbolService.batchDeleteDataSchemeColumnSymbol(id);
        if (this.deleteById(id) > 0) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

    /**
     * 根据菜单资源id获取数据方案字段信息集合
     *
     * @param menuResourceId 菜单资源id
     * @return 数据方案字段信息集合
     */
    public DataSchemeColumnMap getDataSchemeColumnMap(@NotNull(message = "menuResourceId不能为空") Long menuResourceId) {
        DataSchemeColumnMap map = new DataSchemeColumnMap();
        List<DataSchemeColumnAndSymbolVO> columnAndSymbolVOList = dao.getDataSchemeColumnAndSymbolVOList(menuResourceId);
        if (CollectionUtil.isEmpty(columnAndSymbolVOList)) {
            return map;
        }
        Map<String, DataSchemeColumnDetailVO> columnDetailMap = new HashMap<>();
        for (DataSchemeColumnAndSymbolVO columnAndSymbolVO : columnAndSymbolVOList) {
            if (columnDetailMap.containsKey(columnAndSymbolVO.getColumnName())) {
                columnDetailMap.get(columnAndSymbolVO.getColumnName()).getConditionSymbolList().add(columnAndSymbolVO.getConditionSymbol());
            } else {
                DataSchemeColumnDetailVO columnDetailVO = new DataSchemeColumnDetailVO();
                columnDetailVO.setColumnName(columnAndSymbolVO.getColumnName());
                columnDetailVO.setColumnCode(columnAndSymbolVO.getColumnCode());
                columnDetailVO.setColumnType(columnAndSymbolVO.getColumnType());
                columnDetailVO.setConditionContent(columnAndSymbolVO.getConditionContent());
                List<String> conditionSymbolList = new ArrayList<>();
                conditionSymbolList.add(columnAndSymbolVO.getConditionSymbol());
                columnDetailVO.setConditionSymbolList(conditionSymbolList);
                columnDetailMap.put(columnAndSymbolVO.getColumnName(), columnDetailVO);
            }
        }
        map.setColumnDetailMap(columnDetailMap);
        map.setColumnNameList(new ArrayList<>(columnDetailMap.keySet()));
        return map;
    }

    /**
     * 批量删除菜单资源id关联的数据方案字段及条件符号关联表
     *
     * @param menuResourceId
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteSchemeColumnAndSymbol(@NotNull(message = "menuResourceId不能为空") Long menuResourceId) {
        List<DataSchemeColumnAndSymbolIdVO> columnAndSymbolIdVOList = dao.getDataSchemeColumnAndSymbolIdVOList(menuResourceId);
        if (CollectionUtil.isEmpty(columnAndSymbolIdVOList)) {
            return;
        }
        Set<Long> dataSchemeColumnIdSet = new HashSet<>();
        Set<Long> dataSchemeColumnSymbolIdSet = new HashSet<>();
        for (DataSchemeColumnAndSymbolIdVO columnAndSymbolIdVO : columnAndSymbolIdVOList) {
            dataSchemeColumnIdSet.add(columnAndSymbolIdVO.getDataSchemeColumnId());
            dataSchemeColumnSymbolIdSet.add(columnAndSymbolIdVO.getDataSchemeColumnSymbolId());
        }
        List<Long> dataSchemeColumnIdList = new ArrayList<>(dataSchemeColumnIdSet);
        List<Long> dataSchemeColumnSymbolIdList = new ArrayList<>(dataSchemeColumnSymbolIdSet);

        this.deleteBatchIds(dataSchemeColumnIdList);
        dataSchemeColumnSymbolService.deleteBatchIds(dataSchemeColumnSymbolIdList);
    }

}

package pro.wuan.privilege.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.BaseQueryValue;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.convertor.db2page.DbConvert2Page;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.OrgBusinessResourceModel;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeColumnModel;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeModel;
import pro.wuan.privilege.mapper.OrgBusinessResourceMapper;
import pro.wuan.privilege.mapper.OrgDataSchemeColumnMapper;
import pro.wuan.privilege.mapper.OrgDataSchemeMapper;
import pro.wuan.privilege.service.IOrgDataSchemeService;
import pro.wuan.privilege.vo.ConditionElement;
import pro.wuan.privilege.vo.ConditionGroup;
import pro.wuan.privilege.vo.OrgDataSchemeVO;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 数据方案
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Service("orgDataSchemeService")
public class OrgDataSchemeServiceImpl extends BaseServiceImpl<OrgDataSchemeMapper, OrgDataSchemeModel> implements IOrgDataSchemeService {

    @Resource
    private OrgDataSchemeColumnMapper dataSchemeColumnMapper;

    @Resource
    private OrgBusinessResourceMapper businessResourceMapper;

    /**
     * 分页查询数据方案字段
     *
     * @param pageSearchParam 分页查询内容
     * @return 数据方案字段实体分页列表
     */
    public Result<IPage<OrgDataSchemeColumnModel>> selectDataSchemeColumnPage(PageSearchParam pageSearchParam) {
        Page<OrgDataSchemeColumnModel> mppage = new Page<>(pageSearchParam.getPage(), pageSearchParam.getLimit());
        Map<String, BaseQueryValue> queryPrams = pageSearchParam.getQueryPrams();
        if (!queryPrams.containsKey("menuResourceId")) {
            return Result.failure("menuResourceId不能为空");
        }
        BaseQueryValue query_menuResourceId = queryPrams.get("menuResourceId");
        Object[] values_menuResourceId = query_menuResourceId.getValues();
        if (values_menuResourceId == null || values_menuResourceId.length == 0) {
            return Result.failure("menuResourceId不能为空");
        }
        Long menuResourceId = Long.parseLong(values_menuResourceId[0].toString());
        IPage<OrgDataSchemeColumnModel> iPage = dataSchemeColumnMapper.selectPage(mppage, pageSearchParam, menuResourceId);
        List<OrgDataSchemeColumnModel> list = iPage.getRecords();
        if (CollectionUtil.isNotEmpty(list)) {
            DbConvert2Page dbConvert2Page = new DbConvert2Page();
            dbConvert2Page.executeDbToPage4List(list);
        }
        return Result.success(iPage);
    }

    /**
     * 保存数据方案
     *
     * @param schemeVO 数据方案VO
     * @return 数据方案实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgDataSchemeModel> saveDataScheme(OrgDataSchemeVO schemeVO) {
        this.setOrgDataSchemeVO(schemeVO); // 设置数据方案VO
        if (this.insert(schemeVO) > 0) {
            return Result.success(schemeVO);
        } else {
            return Result.failure(schemeVO);
        }
    }

    /**
     * 更新数据方案
     *
     * @param schemeVO 数据方案VO
     * @return 数据方案实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgDataSchemeModel> updateDataScheme(OrgDataSchemeVO schemeVO) {
        this.setOrgDataSchemeVO(schemeVO); // 设置数据方案VO
        if (this.updateById(schemeVO) == 1) {
            return Result.success(schemeVO);
        } else {
            return Result.failure(schemeVO);
        }
    }

    /**
     * 设置数据方案VO
     *
     * @param schemeVO
     */
    private void setOrgDataSchemeVO(OrgDataSchemeVO schemeVO) {
        Assert.notNull(schemeVO.getMenuResourceId(), "菜单资源id不能为空");
        OrgBusinessResourceModel menu = businessResourceMapper.selectById(schemeVO.getMenuResourceId());
        Assert.notNull(menu, "菜单资源不存在");
        Assert.notNull(menu.getAppId(), "菜单资源关联的应用id不能为空");
        Assert.isTrue(CommonConstant.BUSINESS_RESOURCE_TYPE.MENU.getValue().equals(menu.getType()), "只能关联菜单资源");

        if (CommonConstant.DATA_SCHEME_TYPE.TEMPLATE.getValue().equals(schemeVO.getType())) { // 类型为模板
            List<ConditionGroup> conditionGroupList = schemeVO.getConditionGroupList();
            if (CollectionUtil.isEmpty(conditionGroupList)) {
                return;
            }
            schemeVO.setConditionJson(JSONArray.toJSONString(schemeVO.getConditionGroupList()));
            schemeVO.setConditionExpression(this.parseConditionGroupList(schemeVO.getConditionGroupList()));
        } else { // 类型为自定义
            if (StrUtil.isEmpty(schemeVO.getConditionJson())) {
                return;
            }
            try {
                List<ConditionGroup> conditionGroupList = JSONObject.parseObject(schemeVO.getConditionJson(), new TypeReference<List<ConditionGroup>>() {
                });
                schemeVO.setConditionExpression(this.parseConditionGroupList(conditionGroupList));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("自定义方案需按json规范填写");
            }
        }
    }

    /**
     * 解析条件分组列表
     *
     * @param conditionGroupList
     * @return
     */
    public String parseConditionGroupList(List<ConditionGroup> conditionGroupList) {
        StringBuilder conditionExpression = new StringBuilder();
        if (CollectionUtil.isEmpty(conditionGroupList)) {
            return conditionExpression.toString();
        }
        for (int index = 0; index < conditionGroupList.size(); index++) {
            ConditionGroup conditionGroup = conditionGroupList.get(index);
            List<ConditionElement> elementList = conditionGroup.getElementList();
            if (CollectionUtil.isEmpty(elementList)) {
                continue;
            }
            if (index > 0) {
                conditionExpression.append(CommonConstant.STRING_SPACE).append(conditionGroup.getOperation()).append(CommonConstant.STRING_SPACE);
            }
            if (elementList.size() > 1) {
                conditionExpression.append(CommonConstant.SYMBOL_LEFT_BRACKET);
            }
            for (int indexElement = 0; indexElement < elementList.size(); indexElement++) {
                if (indexElement > 0) {
                    conditionExpression.append(CommonConstant.STRING_SPACE).append(CommonConstant.COLUMN_CONNECTOR.AND.getValue()).append(CommonConstant.STRING_SPACE);
                }
                ConditionElement element = elementList.get(indexElement);
                switch (element.getConditionSymbol()){
                    // 碰到exsits/not exists需要忽略字段，直接跟上SQL表达式
                    case "exists":
                    case "not exists":
                        conditionExpression.append(element.getConditionSymbol());
                        conditionExpression.append(CommonConstant.STRING_SPACE);
                        conditionExpression.append(element.getColumnValue());
                    break;
                    default:
                        conditionExpression.append(element.getColumnName());
                        conditionExpression.append(CommonConstant.STRING_SPACE);
                        conditionExpression.append(element.getConditionSymbol());
                        conditionExpression.append(CommonConstant.STRING_SPACE);
                        conditionExpression.append(element.getColumnValue());
                }
            }
            if (elementList.size() > 1) {
                conditionExpression.append(CommonConstant.SYMBOL_RIGHT_BRACKET);
            }
        }
        return conditionExpression.toString();
    }

    /**
     * 删除数据方案
     *
     * @param id 数据方案id
     * @return 删除成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteDataScheme(@NotNull(message = "id不能为空") Long id) {
        if (this.deleteById(id) > 0) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

    /**
     * 根据用户id，应用id获取关联的数据方案列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 数据方案列表
     */
    public List<OrgDataSchemeModel> getDataSchemeListByUserAndApp(@NotNull(message = "userId不能为空") Long userId, @NotNull(message = "appId不能为空") Long appId) {
        return dao.getDataSchemeListByUserAndApp(userId, appId);
    }

    /**
     * 根据角色id列表获取关联的数据方案列表
     *
     * @param roleIdList 角色id列表
     * @return 数据方案列表
     */
    public List<OrgDataSchemeModel> getDataSchemeListByRoleIdList(@NotEmpty(message = "roleIdList不能为空") List<Long> roleIdList) {
        return dao.getDataSchemeListByRoleIdList(roleIdList);
    }

    /**
     * 批量删除菜单资源id关联的数据方案
     *
     * @param menuResourceId
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteDataScheme(@NotNull(message = "menuResourceId不能为空") Long menuResourceId) {
        LambdaQueryWrapper<OrgDataSchemeModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgDataSchemeModel::getMenuResourceId, menuResourceId);
        return this.delete(wrapper);
    }

}

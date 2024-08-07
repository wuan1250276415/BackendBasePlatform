package pro.wuan.privilege.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.db.service.DataFilterService;
import pro.wuan.common.web.constant.ConstantUser;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeModel;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.privilege.service.IOrgDataSchemeService;
import pro.wuan.privilege.vo.ConditionGroup;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据权限过滤
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Service("dataFilterService")
public class DataFilterServiceImpl implements DataFilterService {

    @Resource
    private IOrgDataSchemeService dataSchemeService;

    /**
     * 获取sql查询条件, feign接口调用
     *
     * @param url
     * @return
     */
    @Override
    public String getSql(String url) {
        return this.getSql(url, ConstantUser.getSystemParam());
    }

    /**
     * 获取sql查询条件
     *
     * @param url 请求访问路径
     * @return
     */
    @Override
    public String getSql(String url, Map<String, Object> systemParams) {
        String allData = "1=1";
        String noData = "1=0";
        // 获取当前登录用户信息
        UserDetails userDetails = (UserDetails) systemParams.get(CommonConstant.DATA_FILTER_SYSTEM_PARAMS_USER_DETAILS);
        String userType = userDetails.getUser().getType();
        if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(userType)) { // 超级管理员
            return allData;
        }
        StringBuilder sql = new StringBuilder();
        if (CollectionUtil.isEmpty(userDetails.getUrls()) || !userDetails.getUrls().contains(url)) {
//            throw new IllegalArgumentException("当前用户无权访问路径：" + url);
            return noData;
        }
        if (CollectionUtil.isEmpty(userDetails.getDataSchemeList())) {
            return noData;
        }
        List<String> conditionJsonList = new ArrayList<>();
        List<OrgDataSchemeModel> dataSchemeList = new ArrayList<>();
        for (OrgDataSchemeModel dataScheme : userDetails.getDataSchemeList()) {
            if (url.equals(dataScheme.getUrl())) {
                dataSchemeList.add(dataScheme);
            }
        }
        if (CollectionUtil.isEmpty(dataSchemeList)) {
            return noData;
        }
        for (OrgDataSchemeModel dataScheme : dataSchemeList) {
            String conditionJson = this.parseParameter(dataScheme, userDetails);
            conditionJson = customParseParameter(conditionJson);
            if (StrUtil.isNotEmpty(conditionJson)) {
                conditionJsonList.add(conditionJson);
            }
        }
        if (CollectionUtil.isEmpty(conditionJsonList)) {
            return noData;
        }
        for (int i = 0; i < conditionJsonList.size(); i++) {
            if (i > 0) {
                sql.append(" and "); // 数据方案之间使用and进行连接
            }
            sql.append(CommonConstant.SYMBOL_LEFT_BRACKET).append(this.parseConditionJson(conditionJsonList.get(i))).append(CommonConstant.SYMBOL_RIGHT_BRACKET);
        }
        if (StrUtil.isEmpty(sql)) {
            return noData;
        }
        return sql.toString();
    }

    /**
     * 扩展额外的数据权限替换，需要override当前方法。
     *
     * @param conditionJson
     * @return
     */
    @Override
    public String customParseParameter(String conditionJson) {
        UserDetails userDetails = UserContext.getCurrentUser();
        //直接下属
        List<Long> directUnderlingUserIds = userDetails.getDirectUnderlingUserIds();
        String directUnderlingUser = "-1";
        if (directUnderlingUserIds.size() > 0) {
            directUnderlingUser = String.join(",", Lists.transform(directUnderlingUserIds, new Function<Long, CharSequence>() {
                @Nullable
                @Override
                public CharSequence apply(@Nullable Long input) {
                    return input.toString();
                }
            }));
        }
        conditionJson = conditionJson.replace("@一级下属", directUnderlingUser);

        //间接下属
        List<Long> inDirectUnderlingUserIds = userDetails.getIndirectUnderlingUserIds();
        String inDirectUnderlingUser = "-1";
        if (inDirectUnderlingUserIds.size() > 0) {
            inDirectUnderlingUser = String.join(",", Lists.transform(inDirectUnderlingUserIds, new Function<Long, CharSequence>() {
                @Nullable
                @Override
                public CharSequence apply(@Nullable Long input) {
                    return input.toString();
                }
            }));
        }
        conditionJson = conditionJson.replace("@二级下属", inDirectUnderlingUser);

        //单位ID
        Long orgOrgan = -1L;
        OrgOrganModel orgOrganModel = userDetails.getUnit();
        if(null != orgOrganModel){
            orgOrgan = orgOrganModel.getId();
        }
        conditionJson = conditionJson.replace("@所属单位", orgOrgan.toString());


        //部门ID
        Long deptId = -1L;
        OrgOrganModel departmentModel = userDetails.getDepartment();
        if(null != departmentModel){
            deptId = departmentModel.getId();
        }
        conditionJson = conditionJson.replace("@所属部门", deptId.toString());

        return conditionJson;
    }

    /**
     * 解析参数
     *
     * @param dataScheme
     * @param userDetails
     * @return
     */
    private String parseParameter(OrgDataSchemeModel dataScheme, UserDetails userDetails) {
        String conditionJson = dataScheme.getConditionJson();
        if (StrUtil.isEmpty(conditionJson)) {
            return conditionJson;
        }
        Long invalidId = -1L;
        OrgUserModel user = userDetails.getUser();
        Long userId = (user != null) ? user.getId() : invalidId;
        conditionJson = conditionJson.replace(CommonConstant.COLUMN_CONDITION_CONTENT.CURRENT_USER.getValue(), String.valueOf(userId));
        OrgOrganModel unit = userDetails.getUnit();
        Long unitId = (unit != null) ? unit.getId() : invalidId;
        conditionJson = conditionJson.replace(CommonConstant.COLUMN_CONDITION_CONTENT.CURRENT_UNIT.getValue(), String.valueOf(unitId));
        OrgOrganModel department = userDetails.getDepartment();
        Long departmentId = (department != null) ? department.getId() : invalidId;
        conditionJson = conditionJson.replace(CommonConstant.COLUMN_CONDITION_CONTENT.CURRENT_DEPT.getValue(), String.valueOf(departmentId));
        return conditionJson;
    }

    private String parseConditionJson(String conditionJson) {
        List<ConditionGroup> conditionGroupList = JSONObject.parseObject(conditionJson, new TypeReference<List<ConditionGroup>>() {
        });
        return dataSchemeService.parseConditionGroupList(conditionGroupList);
    }

}

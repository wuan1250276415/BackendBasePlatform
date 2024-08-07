package pro.wuan.privilege.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.OrgBusinessResourceModel;
import pro.wuan.feignapi.userapi.entity.OrgListColumnModel;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.privilege.mapper.OrgBusinessResourceMapper;
import pro.wuan.privilege.mapper.OrgListColumnMapper;
import pro.wuan.privilege.service.IOrgBusinessResourceService;
import pro.wuan.privilege.service.IOrgListColumnService;
import pro.wuan.privilege.vo.BatchSaveListColumnVO;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 列表字段
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Validated
@Service("orgListColumnService")
public class OrgListColumnServiceImpl extends BaseServiceImpl<OrgListColumnMapper, OrgListColumnModel> implements IOrgListColumnService {

    @Resource
    private OrgBusinessResourceMapper businessResourceMapper;

    @Resource
    private IOrgBusinessResourceService businessResourceService;

    /**
     * 批量新增字段实体
     *
     * @param batchSaveListColumnVO 批量新增字段实体VO
     * @return 新增成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> batchSaveListColumn(BatchSaveListColumnVO batchSaveListColumnVO) {
        List<OrgListColumnModel> listColumnModelList = JSONObject.parseArray(batchSaveListColumnVO.getJson(), OrgListColumnModel.class);
        if (CollectionUtil.isEmpty(listColumnModelList)) {
            return Result.failure("批量新增失败");
        }
        Long menuResourceId = batchSaveListColumnVO.getMenuResourceId();
        Assert.notNull(businessResourceMapper.selectById(menuResourceId), "菜单资源不存在");
        Set<String> columnNameSet = new HashSet<>();
        String columnName = null;
        for (OrgListColumnModel listColumnModel : listColumnModelList) {
            columnName = listColumnModel.getColumnName();
            Assert.hasLength(columnName, "字段名称不能为空");
            Assert.hasLength(listColumnModel.getColumnCode(), "字段注解不能为空");
            Assert.isTrue(!columnNameSet.contains(columnName), "字段名称不能重复：" + columnName);
            Assert.isTrue(!this.isExistColumnName(null, menuResourceId, columnName), "字段名称已经存在，请重新输入");
            listColumnModel.setMenuResourceId(menuResourceId);
            listColumnModel.setStatus(CommonConstant.SHOW_STATUS.SHOW.getValue());
            this.save(listColumnModel);
            columnNameSet.add(columnName);
        }
        return Result.success("批量新增成功");
    }

    /**
     * 字段名称是否存在
     *
     * @param id id
     * @param menuResourceId 菜单资源id
     * @param columnName 字段名称
     * @return true/false
     */
    public boolean isExistColumnName(Long id, Long menuResourceId, String columnName) {
        LambdaQueryWrapper<OrgListColumnModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgListColumnModel::getMenuResourceId, menuResourceId);
        wrapper.eq(OrgListColumnModel::getColumnName, columnName);
        if (id != null) {
            wrapper.ne(OrgListColumnModel::getId, id);
        }
        Long count = dao.selectCount(wrapper);
        return count != null && count > 0;
    }

    /**
     * 根据应用id获取关联的列表字段列表
     *
     * @param appId 应用id
     * @return 列表字段列表
     */
    public List<OrgListColumnModel> getListColumnListByAppId(@NotNull(message = "appId不能为空") Long appId) {
        return dao.getListColumnListByAppId(appId);
    }

    /**
     * 根据用户id，应用id获取关联的列表字段列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 列表字段列表
     */
    public List<OrgListColumnModel> getListColumnListByUserAndApp(@NotNull(message = "userId不能为空") Long userId, @NotNull(message = "appId不能为空") Long appId) {
        return dao.getListColumnListByUserAndApp(userId, appId);
    }

    /**
     * 根据角色id列表获取关联的列表字段列表
     *
     * @param roleIdList 角色id列表
     * @return 列表字段列表
     */
    public List<OrgListColumnModel> getListColumnListByRoleIdList(@NotEmpty(message = "roleIdList不能为空") List<Long> roleIdList) {
        return dao.getListColumnListByRoleIdList(roleIdList);
    }

    /**
     * 批量删除菜单资源id关联的列表字段
     *
     * @param menuResourceId
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteListColumn(@NotNull(message = "menuResourceId不能为空") Long menuResourceId) {
        LambdaQueryWrapper<OrgListColumnModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgListColumnModel::getMenuResourceId, menuResourceId);
        return this.delete(wrapper);
    }

    /**
     * 根据菜单url获取当前用户的列表字段列表
     *
     * @param url 菜单url
     * @return 列表字段列表
     */
    @Deprecated
    public List<OrgListColumnModel> getListColumnListByCurrentUser(@NotBlank(message = "url不能为空") String url) {
        UserDetails userDetails = UserContext.getCurrentUser();
        List<OrgBusinessResourceModel> menuTree = userDetails.getMenuList();
        List<OrgBusinessResourceModel> menuList = businessResourceService.getMenuList(menuTree);
        List<OrgListColumnModel> listColumnList = userDetails.getListColumnList();
        if (CollectionUtil.isEmpty(menuList) || CollectionUtil.isEmpty(listColumnList)) {
            return new ArrayList<>();
        }
        List<Long> matchMenuIdList = new ArrayList<>();
        menuList.stream().forEach(menu -> {
            if (url.equals(menu.getUrl())) {
                matchMenuIdList.add(menu.getId());
            }
        });
        if (CollectionUtil.isEmpty(matchMenuIdList)) {
            return new ArrayList<>();
        }
        List<OrgListColumnModel> list = listColumnList.stream().filter(listColumn -> matchMenuIdList.contains(listColumn.getMenuResourceId())).collect(Collectors.toList());
        return list;
    }


    /**
     * 根据菜单url获取当前用户的列表字段列表
     *
     * @param url 菜单url
     * @return 列表字段列表
     */
    public List<OrgListColumnModel> getColumnListFilterByCurrentUser(@NotBlank(message = "url不能为空") String url) {
        UserDetails userDetails = UserContext.getCurrentUser();
        List<OrgListColumnModel> listColumnList = userDetails.getListColumnList();

        if (CollectionUtil.isEmpty(listColumnList)) {
            return Collections.EMPTY_LIST;
        }
        List<OrgListColumnModel> list = listColumnList.stream().filter(listColumn -> url.equalsIgnoreCase(listColumn.getGroupName())).collect(Collectors.toList());
        return list;
    }


}

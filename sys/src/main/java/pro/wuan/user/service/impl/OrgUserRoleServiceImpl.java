package pro.wuan.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.element.mapper.OrgElementMapper;
import pro.wuan.feignapi.userapi.entity.OrgElementModel;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;
import pro.wuan.feignapi.userapi.entity.OrgRoleModel;
import pro.wuan.feignapi.userapi.entity.OrgUserRoleModel;
import pro.wuan.organ.mapper.OrgOrganMapper;
import pro.wuan.role.mapper.OrgRoleMapper;
import pro.wuan.user.mapper.OrgUserRoleMapper;
import pro.wuan.user.service.IOrgUserRoleService;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户角色关联表
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
@Validated
@Service("orgUserRoleService")
public class OrgUserRoleServiceImpl extends BaseServiceImpl<OrgUserRoleMapper, OrgUserRoleModel> implements IOrgUserRoleService {

    @Resource
    private OrgRoleMapper roleMapper;

    @Resource
    private OrgOrganMapper organMapper;

    @Resource
    private OrgElementMapper elementMapper;

    /**
     * 批量保存用户角色关联表
     *
     * @param userId
     * @param roleIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveUserRole(@NotNull(message = "userId不能为空") Long userId, List<Long> roleIdList) {
        if (CollectionUtil.isNotEmpty(roleIdList)) {
            List<OrgUserRoleModel> userRoleList = new ArrayList<>();
            for (Long roleId : roleIdList) {
                OrgRoleModel roleModel = roleMapper.selectById(roleId);
                if (roleModel != null) {
                    OrgUserRoleModel userRoleModel = new OrgUserRoleModel();
                    userRoleModel.setUserId(userId);
                    userRoleModel.setRoleId(roleId);
                    userRoleList.add(userRoleModel);
                }
            }
            this.batchInsertNoCascade(userRoleList);
        }
    }

    /**
     * 批量删除用户角色关联表
     *
     * @param userId
     * @param roleIdList
     * @return
     */
    public int batchDeleteUserRole(@NotNull(message = "userId不能为空") Long userId, List<Long> roleIdList) {
        LambdaQueryWrapper<OrgUserRoleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgUserRoleModel::getUserId, userId);
        wrapper.in(OrgUserRoleModel::getRoleId, roleIdList);
        return this.delete(wrapper);
    }

    /**
     * 批量删除用户角色关联表
     *
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteUserRole(@NotNull(message = "userId不能为空") Long userId) {
        LambdaQueryWrapper<OrgUserRoleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgUserRoleModel::getUserId, userId);
        return this.delete(wrapper);
    }

    /**
     * 批量保存角色用户关联表
     *
     * @param roleId
     * @param userIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Result batchSaveRoleUser(@NotNull(message = "roleId不能为空") Long roleId, List<Long> userIdList) {
        if (CollectionUtil.isEmpty(userIdList)) {
            return Result.success("保存成功");
        }
        Set<Long> userIdSet = new HashSet<>();
        for (Long userId : userIdList) {
            userIdSet.add(userId);
        }
        OrgRoleModel role = roleMapper.selectById(roleId);
        if (role == null) {
            return Result.failure("角色信息不存在");
        }
        LambdaQueryWrapper<OrgElementModel> eWrapper = new LambdaQueryWrapper<>();
        eWrapper.eq(OrgElementModel::getType, CommonConstant.ELEMENT_TYPE.USER.getValue());
        eWrapper.in(OrgElementModel::getId, userIdSet);
        List<OrgElementModel> elementList = elementMapper.selectList(eWrapper);
        if (CollectionUtil.isEmpty(elementList)) {
            return Result.failure("用户列表关联的元素信息不存在");
        }
        // 非单位管理员角色
        if (!CommonConstant.ROLE_ID_UNIT_ADMIN.equals(role.getId())) {
            OrgOrganModel unit = organMapper.selectById(role.getOrganId());
            if (unit == null) {
                return Result.failure("角色所属单位信息不存在");
            }
            String unitIdString = unit.getId().toString();
            boolean containOtherUnit = false;
            for (OrgElementModel element : elementList) {
                if (!element.getTreeIds().contains(unitIdString)) {
                    containOtherUnit = true;
                    break;
                }
            }
            if (containOtherUnit) {
                return Result.failure("角色只能关联所属单位[" + unit.getName() + "]下的用户");
            }
        }

        // 排除已经存在的角色用户关联信息
        LambdaQueryWrapper<OrgUserRoleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgUserRoleModel::getRoleId, roleId);
        wrapper.in(OrgUserRoleModel::getUserId, userIdSet);
        List<OrgUserRoleModel> userRoleModelList = this.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(userRoleModelList)) {
            List<Long> oldUserIdList = new ArrayList<>();
            for (OrgUserRoleModel userRoleModel : userRoleModelList) {
                oldUserIdList.add(userRoleModel.getUserId());
            }
            userIdSet.removeAll(oldUserIdList);
        }
        // 批量保存角色用户关联信息
        List<OrgUserRoleModel> userRoleList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(userIdSet)) {
            for (Long userId : userIdSet) {
                OrgUserRoleModel userRoleModel = new OrgUserRoleModel();
                userRoleModel.setUserId(userId);
                userRoleModel.setRoleId(roleId);
                userRoleList.add(userRoleModel);
            }
            this.batchInsertNoCascade(userRoleList);
        }
        return Result.success("保存成功");
    }

    /**
     * 批量删除角色用户关联表
     *
     * @param roleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteRoleUser(@NotNull(message = "roleId不能为空") Long roleId) {
        LambdaQueryWrapper<OrgUserRoleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgUserRoleModel::getRoleId, roleId);
        return this.delete(wrapper);
    }

    /**
     * 根据用户id查询用户角色关联实体信息
     *
     * @param userId
     * @return
     */
    public List<OrgUserRoleModel> getUserRoleListByUserId(@NotNull(message = "userId不能为空") Long userId) {
        LambdaQueryWrapper<OrgUserRoleModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrgUserRoleModel::getUserId, userId);
        List<OrgUserRoleModel> modelList = this.selectList(queryWrapper);
        return modelList;
    }

    /**
     * 根据角色id查询用户角色关联实体信息
     *
     * @param roleId
     * @return
     */
    public List<OrgUserRoleModel> getUserRoleListByRoleId(@NotNull(message = "roleId不能为空") Long roleId) {
        LambdaQueryWrapper<OrgUserRoleModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrgUserRoleModel::getRoleId, roleId);
        List<OrgUserRoleModel> modelList = this.selectList(queryWrapper);
        return modelList;
    }

    /**
     * 根据角色id列表查询用户角色关联实体信息
     *
     * @param roleIdList
     * @return
     */
    public List<OrgUserRoleModel> getUserRoleListByRoleIdList(List<Long> roleIdList) {
        if (CollectionUtil.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<OrgUserRoleModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OrgUserRoleModel::getRoleId, roleIdList);
        List<OrgUserRoleModel> modelList = this.selectList(queryWrapper);
        return modelList;
    }

    /**
     * 根据单位id集合和角色名称集合获取用户角色列表
     *
     * @param organIds     机构id集合
     * @param roleNameList 角色名称集合
     * @return
     */
    public List<OrgUserRoleModel> getUserRoleList(List<Long> organIds, List<String> roleNameList) {
        List<Long> roleIdList = this.getRoleIdList(organIds,roleNameList);
        return this.getUserRoleListByRoleIdList(roleIdList);
    }

    /**
     * 获取角色id集合
     * @param organIds
     * @param roleNameList
     * @return
     */
    public List<Long> getRoleIdList(List<Long> organIds, List<String> roleNameList){
        LambdaQueryWrapper<OrgRoleModel> roleQuery = new LambdaQueryWrapper<>();
        roleQuery.in(OrgRoleModel::getOrganId, organIds);
        roleQuery.in(OrgRoleModel::getName, roleNameList);
        List<OrgRoleModel> roles = roleMapper.selectList(roleQuery);
        if (CollectionUtil.isEmpty(roles)) {
            return new ArrayList<>();
        }
        List<Long> roleIdList = roles.stream().map(y -> y.getId()).collect(Collectors.toList());
        return roleIdList;
    }

}

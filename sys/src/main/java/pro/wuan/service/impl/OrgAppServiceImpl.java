package pro.wuan.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.appapi.OrgAppModel;
import pro.wuan.feignapi.userapi.entity.OrgRoleModel;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;
import pro.wuan.mapper.OrgAppMapper;
import pro.wuan.role.mapper.OrgRoleMapper;
import pro.wuan.service.IOrgAppService;
import pro.wuan.user.mapper.OrgUserMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 应用
 *
 * @author: liumin
 * @date: 2021-08-30 11:22:48
 */
@Service("orgAppService")
public class OrgAppServiceImpl extends BaseServiceImpl<OrgAppMapper, OrgAppModel> implements IOrgAppService {

    @Resource
    private OrgUserMapper userMapper;

    @Resource
    private OrgRoleMapper roleMapper;

    /**
     * 编码是否存在
     *
     * @param id id
     * @param code 编码
     * @return true/false
     */
    public boolean isExistCode(Long id, String code) {
        LambdaQueryWrapper<OrgAppModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgAppModel::getCode, code);
        if (id != null) {
            wrapper.ne(OrgAppModel::getId, id);
        }
        Long count = dao.selectCount(wrapper);
        return (count != null && count > 0) ? true : false;
    }

    /**
     * 根据应用id获取应用
     *
     * @param id 应用id
     * @return 应用
     */
    public OrgAppModel getAppById(Long id) {
        return this.selectById(id);
    }

    /**
     * 根据编码获取应用
     *
     * @param code 编码
     * @return 应用
     */
    public OrgAppModel getAppByCode(String code) {
        LambdaQueryWrapper<OrgAppModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgAppModel::getCode, code);
        wrapper.orderByAsc(OrgAppModel::getId);
        List<OrgAppModel> list = this.selectList(wrapper);
        return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
    }

    /**
     * 根据应用id列表获取应用列表
     *
     * @param idList 应用id列表
     * @return 应用列表
     */
    public List<OrgAppModel> getAppListByIdList(List<Long> idList) {
        LambdaQueryWrapper<OrgAppModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrgAppModel::getId, idList);
        wrapper.orderByAsc(OrgAppModel::getSort);
        return this.selectList(wrapper);
    }

    /**
     * 根据用户id获取应用列表
     *
     * @param userId
     * @return
     */
    public List<OrgAppModel> getAppListByUserId(Long userId) {
        Assert.notNull(userId, "用户id不能为空");
        List<OrgAppModel> appList = new ArrayList<>();
        OrgUserModel user = userMapper.selectById(userId);
        Assert.notNull(user, "用户信息不存在。用户id：" + userId);
        if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 超级管理员
            LambdaQueryWrapper<OrgAppModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrgAppModel::isDeleteFlag, false);
            wrapper.orderByAsc(OrgAppModel::getSort);
            appList = dao.selectList(wrapper);
            return appList;
        }
        // 获取用户角色列表
        List<OrgRoleModel> roleList = roleMapper.getRoleListByUserId(userId, null);
        if (CollectionUtil.isEmpty(roleList)) {
            return appList;
        }
        // 获取角色关联的应用id集合
        Set<Long> appIdSet = new HashSet<>();
        for (OrgRoleModel role : roleList) {
            appIdSet.add(role.getAppId());
        }
        if (CollectionUtil.isEmpty(appIdSet)) {
            return appList;
        }
        List<Long> appIdList = new ArrayList<>(appIdSet);
        appList = this.getAppListByIdList(appIdList);
        return appList;
    }

}

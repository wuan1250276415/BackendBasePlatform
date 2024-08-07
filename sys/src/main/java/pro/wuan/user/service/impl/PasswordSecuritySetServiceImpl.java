package pro.wuan.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.user.config.PasswordSecurityConfig;
import pro.wuan.user.entity.PasswordSecuritySet;
import pro.wuan.user.mapper.PasswordSecuritySetMapper;
import pro.wuan.user.service.IOrgUserService;
import pro.wuan.user.service.IPasswordSecuritySetService;

/**
 * @version V1.0
 * @Title: OrgServiceImpl.java
 * @Description: 密码安全设置管理 业务层
 * @author:
 * @date: 2020/7/27 15:04
 */
@Service
public class PasswordSecuritySetServiceImpl extends BaseServiceImpl<PasswordSecuritySetMapper, PasswordSecuritySet>
        implements IPasswordSecuritySetService {

    @Autowired
    private PasswordSecurityConfig passwordSecurityConfig;
    @Autowired
    private IOrgUserService orgUserService;




    /**
     * 添加密码安全设置
     * @param passwordSecuritySet
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PasswordSecuritySet> save(PasswordSecuritySet passwordSecuritySet) {
        this.updateStatus(passwordSecuritySet);
        if (this.insert(passwordSecuritySet) > 0 ) {
            return Result.success();
        } else {
            return Result.failure();
        }

    }

    /**
     * 修改密码安全设置
     *
     * @param passwordSecuritySet
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PasswordSecuritySet> update(PasswordSecuritySet passwordSecuritySet) {
        this.updateStatus(passwordSecuritySet);
        this.updateById(passwordSecuritySet);
        return Result.success();
    }

    /**
     * 将其他的改为不启用
     * @param passwordSecuritySet
     */
    public void updateStatus(PasswordSecuritySet passwordSecuritySet) {
       /* if (ApplicationUtil.YesOrNo.YES.equals(passwordSecuritySet.getStatus())) {
            QueryWrapper<PasswordSecuritySet> queryWrapper = new QueryWrapper();
            queryWrapper.eq("status", ApplicationUtil.YesOrNo.YES);
            queryWrapper.eq("tenant_id", UserContext.getCurrentUserTenantId());
            PasswordSecuritySet set = new PasswordSecuritySet();
            set.setStatus(ApplicationUtil.YesOrNo.NO);
            //将之前是否启用为是的改为否
            this.update(set, queryWrapper);
        }*/
    }

    /**
     * 创建默认安全设置
     *
     * @return
     */
    @Override
    public Boolean createDefaultSet(Long tenantId) {
        PasswordSecuritySet passwordSecuritySet = new PasswordSecuritySet();
        passwordSecuritySet.setPasswordStrength(passwordSecurityConfig.getPasswordStrength());
        passwordSecuritySet.setFailureCount(passwordSecurityConfig.getFailureCount());
        passwordSecuritySet.setForbidTerm(passwordSecurityConfig.getForbidTerm());
        passwordSecuritySet.setModifyTerm(passwordSecurityConfig.getModifyTerm());
        passwordSecuritySet.setEnforceChange(passwordSecurityConfig.getEnforceChange());
        passwordSecuritySet.setPasswordConsistent(passwordSecurityConfig.getPasswordConsistent());
        passwordSecuritySet.setInitPassword(passwordSecurityConfig.getInitPassword());
        return this.insert(passwordSecuritySet) > 0 ? true : false;
    }

    /**
     * 是否需要强制修改密码
     * @return
     */
    @Override
    public Boolean updatePwd() {
        /*PasswordSecuritySet passwordSecuritySet = this.selectOne(UserContext.getCurrentUserTenantId());
        if (passwordSecuritySet == null) {
            return false;
        }
        OrgUser orgUser = orgUserService.selectById(UserContext.getCurrentUserId());
        //需要强制修改密码
        if (ApplicationUtil.YesOrNo.YES.equals(passwordSecuritySet.getEnforceChange()) &&
                StringUtils.isNotEmpty(passwordSecuritySet.getInitPassword()) &&
                orgUser.getPassword().equals(MD5Encoder.getMD5String(passwordSecuritySet.getInitPassword()))) {
                return true;
        }
        int modifyTerm = passwordSecuritySet.getModifyTerm();
        Date modifyPasswordDate = orgUser.getModifyPasswordDate();
        long day = (System.currentTimeMillis() - modifyPasswordDate.getTime()) / (24 * 60 * 60 * 1000);
        if (day > modifyTerm) {
            return true;
        }
        return false;*/

        return true;
    }



    /**
     * 获取系统密码安全策略
     * @return
     */
    @Override
    public PasswordSecuritySet getSecuritySet() {
        QueryWrapper<PasswordSecuritySet> queryWrapper=new QueryWrapper();
        return this.selectOne(queryWrapper);
    }




}

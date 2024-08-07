package pro.wuan.user.service;


import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.user.entity.PasswordSecuritySet;
import pro.wuan.user.mapper.PasswordSecuritySetMapper;

/**
 * @Title:  IPasswordSecuritySetService.java
 * @Description: 密码安全设置管理接口
 * @author:
 * @date:   2020/7/27 15:00
 * @version V1.0
 */
public interface IPasswordSecuritySetService extends IBaseService<PasswordSecuritySetMapper, PasswordSecuritySet> {



    /**
     * 修改密码安全设置
     * @param passwordSecuritySet
     * @return
     */
    Result<PasswordSecuritySet> save(PasswordSecuritySet passwordSecuritySet);

    /**
     * 修改密码安全设置
     * @param passwordSecuritySet
     * @return
     */
    Result<PasswordSecuritySet> update(PasswordSecuritySet passwordSecuritySet);

    /**
     * 创建默认安全设置
     * @return
     */
    Boolean createDefaultSet(Long tenantId);

    /**
     * 是否需要强制修改密码
     * @return
     */
    Boolean updatePwd();


    /**
     * 获取系统密码安全策略
     * @return
     */
    PasswordSecuritySet  getSecuritySet();

}

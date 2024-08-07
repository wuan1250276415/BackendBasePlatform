package pro.wuan.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: oldone
 * @date: 2020/9/23 11:25
 */
@ConfigurationProperties("tellhow.passwordsecurity")
@Component
@Data
public class PasswordSecurityConfig {
    /**
     * 密码强度 1弱；2中；3强；4最好
     */
    private String passwordStrength;
    /**
     * 允许登录失败次数 小于等于0代表不受限制
     */
    private Integer failureCount;
    /**
     * 禁止登录期限 小于等于0代表不受限制，单位小时
     */
    private Integer forbidTerm;
    /**
     * 密码修改定期提醒 小于等于0代表不受限制，单位天
     */
    private Integer modifyTerm;
    /**
     * 密码到期或者因为系统配置是否需要强制修改密码 1需要；0不需要
     */
    private String enforceChange;
    /**
     * 修改密码时是否允许与旧密码一致 1是；0否
     */
    private String passwordConsistent;
    /**
     * 新建账号初始密码
     */
    private String initPassword;
    /**
     * 是否启用 1是，0否
     */
    private String status;

}

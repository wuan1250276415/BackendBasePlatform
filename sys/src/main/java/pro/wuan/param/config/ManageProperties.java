package pro.wuan.param.config;

import lombok.Data;
import org.springframework.stereotype.Component;
import pro.wuan.common.core.annotation.param.*;

/**
 * @program: tellhowcloud
 * @description: 后台管理系统配置项
 * @author: HawkWang
 * @create: 2021-09-08 09:06
 **/
@Component
@Data
@SystemProperty(name = "后台登陆属性")
public class ManageProperties {

    /**
     * 系统名称
     */
    @PropertyString(max = 200, min = 1, defaultValue = "想知道我叫什么吗", name = "系统名称")
    private String systemName;

    /**
     * 需要验证码
     */
    @PropertyRadio(name = "是否需要验证码", defaultValue = "1", options = {@PropertyOption(label = "需要", value = "1"), @PropertyOption(label = "不需要", value = "0")})
    private Boolean needCaptcha;

    /**
     * 错误次数
     */
    @PropertyInteger(name = "错误次数", max = 999)
    private Integer wrongTimes;
}

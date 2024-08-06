package pro.wuan.common.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "tellhow.token")
@Component
@Data
public class JwtProperties {

    /**
     * token过期时间，单位分钟
     */
    Long tokenExpireTime;

    /**
     * 更新令牌时间，单位分钟
     */
    Long refreshCheckTime;
    /**
     * token加密密钥
     */
    String secretKey;
    /**
     * 是否踢人
     */
    Boolean isTick;
    /**
     * token冗余时间,单位毫秒
     */
    Long redundancyTime;
}

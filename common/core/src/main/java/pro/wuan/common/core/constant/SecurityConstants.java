package pro.wuan.common.core.constant;

/**
 * Security 权限常量
 */
public interface SecurityConstants {
    /**
     * 用户信息分隔符
     */
    String USER_SPLIT = ":";

    /**
     * 用户信息头
     */
    String USER_HEADER = "x-user-header";

    /**
     * 用户id信息头
     */
    String USER_ID_HEADER = "x-userid-header";

    /**
     * 角色信息头
     */
    String ROLE_HEADER = "x-role-header";
    /**
     * token
     */
    String REQUEST_AUTH_HEADER = "access-Token";
    /**
     * 请求来源
     */
    String REQUEST_FROM = "from";
    /**
     * 请求来源feign
     */
    String FROM_FEIGN = "feign";

    /**
     * JWT-account
     */
    String ACCOUNT = "account";
    /**
     * 刷新token前缀
     */
    String PREFIX_REFRESH_TOKEN = "tellhow-cloud:refresh_token:";

    /**
     * 刷新检查前缀
     */
    String PREFIX_REFRESH_CHECK = "tellhow:refresh_check:";

    /**
     * JWT-currentTimeMillis
     */
    String CURRENT_TIME_MILLIS = "currentTimeMillis";
}

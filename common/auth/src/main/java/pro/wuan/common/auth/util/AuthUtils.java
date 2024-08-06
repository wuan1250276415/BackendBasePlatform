package pro.wuan.common.auth.util;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.UserAuthInfo;
import pro.wuan.common.redis.util.JedisUtil;

import java.util.Set;

/**
 * @description:
 * @author: oldone
 * @date: 2021/8/18 16:12
 */
@Component
public class AuthUtils {

    private static AuthUtils authUtils;

    @Autowired
    private JedisUtil jedisUtil;

    @PostConstruct
    public void init() {
        authUtils = this;
        authUtils.jedisUtil = this.jedisUtil;
    }

    /**
     * 判断资源是否有访问权限
     *
     * @param account
     * @param path
     * @return
     */
    public static Boolean isPermit(String account, String path) {
        UserAuthInfo userAuthInfo = (UserAuthInfo) authUtils.jedisUtil.get(CommonConstant.JEDIS_USER_AUTH_PREFIX + account);
        if (userAuthInfo == null) {
            return false;
        }
        String userType = userAuthInfo.getUserType();
        // 超级管理员不需要控制访问权限
        if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(userType)) {
            return true;
        }
        Set<String> urls = userAuthInfo.getUrls();
        if (CollectionUtil.isEmpty(urls)) {
            return false;
        }
        if (urls.contains(path)) {
            return true;
        }
        return false;
    }

}

package pro.wuan.common.core.model;

import lombok.Data;

import java.util.Set;

/**
 * 用户权限信息
 *
 * @author: liumin
 * @date: 2021/9/26 15:31
 */
@Data
public class UserAuthInfo {

    /**
     * 用户名
     */
    private String account;

    /**
     * 用户类型。枚举：superAdmin-超级管理员，unitAdmin-单位管理员，commonUser-普通用户
     */
    private String userType;

    /**
     * 可访问路径集合
     */
    private Set<String> urls;

}

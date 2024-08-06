package pro.wuan.common.core.model;

import lombok.Data;

/**
 * 用户基本属性信息
 *
 * @author: oldone
 * @date: 2021/9/26 16:05
 */
@Data
public class BaseUser {

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 部门id
     */
    private Long organId;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 是否超管
     */
    private Boolean isSuperAdmin;
    /**
     * 所属数据库模式
     */
    private String databaseModl;
}

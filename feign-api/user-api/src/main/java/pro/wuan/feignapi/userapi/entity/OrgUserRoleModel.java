package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.model.IDModel;

/**
 * 用户角色关联表
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("org_user_role")
@ServiceType(serviceName = "orgUserRoleService")
public class OrgUserRoleModel extends IDModel {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 角色id
     */
    @TableField(value = "role_id")
    private Long roleId;

}

package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import pro.wuan.common.core.annotation.EnumFiled;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.IDModel;

/**
 * 角色权限关联表
 *
 * @author: liumin
 * @date: 2021-08-30 11:15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("org_role_privilege")
@ServiceType(serviceName = "orgRolePrivilegeService")
public class OrgRolePrivilegeModel extends IDModel {

	private static final long serialVersionUID = 1L;

	/**
	 * 角色id
	 */
	@TableField(value = "role_id")
	private Long roleId;

	/**
	 * 权限id
	 */
	@TableField(value = "privilege_id")
	private Long privilegeId;

	/**
	 * 权限类型。枚举：menu-菜单，function-功能，list-列表，data-数据
	 */
	@EnumFiled(type = CommonConstant.PRIVILEGE_TYPE.class, targetProperty = "typeName")
	@TableField(value = "type")
	private String type;

	@TableField(exist = false)
	private String typeName;

}

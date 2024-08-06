package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pro.wuan.common.core.annotation.EnumFiled;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.BaseDeleteModel;

import jakarta.validation.constraints.*;

/**
 * 列表字段
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("org_list_column")
@ServiceType(serviceName = "orgListColumnService")
public class OrgListColumnModel extends BaseDeleteModel {

	private static final long serialVersionUID = 1L;

	/**
	 * 字段名称
	 */
	@NotBlank(message = "字段名称不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 200, message = "字段名称最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "column_name")
	private String columnName;

	/**
	 * 字段注解
	 */
	@NotBlank(message = "字段注解不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 200, message = "字段注解最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "column_code")
	private String columnCode;

	/**
	 * 显示状态。枚举：0-隐藏，1-显示
	 */
	@NotNull(message = "显示状态不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Range(max = 1, message = "显示状态最大值不能超过1", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@EnumFiled(type = CommonConstant.SHOW_STATUS.class, targetProperty = "statusName")
	@TableField(value = "status")
	private Integer status;

	@TableField(exist = false)
	private String statusName;

	/**
	 * 备注
	 */
	@Length(max = 200, message = "备注最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "remark")
	private String remark;

	/**
	 * 菜单资源id
	 */
	@NotNull(message = "菜单资源id不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "menu_resource_id")
	private Long menuResourceId;

	@Length(max = 200, message = "分组最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "group_name")
	private String groupName;
}

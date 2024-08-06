package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import pro.wuan.common.core.annotation.EnumFiled;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.BaseDeleteModel;
import pro.wuan.feignapi.userapi.JsonValid;

import jakarta.validation.constraints.*;

/**
 * 数据方案
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
@TableName("org_data_scheme")
@ServiceType(serviceName = "orgDataSchemeService")
public class OrgDataSchemeModel extends BaseDeleteModel {

	private static final long serialVersionUID = 1L;

	/**
	 * 方案名称
	 */
	@NotBlank(message = "方案名称不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 200, message = "方案名称最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "name")
	private String name;

	/**
	 * 类型。枚举：template-模板，custom-自定义
	 */
	@NotBlank(message = "类型不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 40, message = "类型最大长度不能超过40", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@EnumFiled(type = CommonConstant.DATA_SCHEME_TYPE.class, targetProperty = "typeName")
	@TableField(value = "type")
	private String type;

	@TableField(exist = false)
	private String typeName;

	/**
	 * 地址
	 */
	@NotBlank(message = "地址名称不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 200, message = "地址最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "url")
	private String url;

	/**
	 * 过滤条件json
	 */
	@Length(max = 1000, message = "过滤条件json最大长度不能超过1000", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@JsonValid(message = "自定义方案需按json规范填写", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "condition_json")
	private String conditionJson;

	/**
	 * 过滤条件表达式
	 */
	@Length(max = 1000, message = "过滤条件表达式最大长度不能超过1000", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "condition_expression")
	private String conditionExpression;

	/**
	 * 菜单资源id
	 */
	@NotNull(message = "菜单资源id不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "menu_resource_id")
	private Long menuResourceId;

}

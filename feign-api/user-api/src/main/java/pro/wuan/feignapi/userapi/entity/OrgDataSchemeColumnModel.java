package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import pro.wuan.common.core.annotation.EnumFiled;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.annotation.constraints.group.ValidatorQueryCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.BaseDeleteModel;

import java.util.List;

/**
 * 数据方案字段
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
@TableName("org_data_scheme_column")
@ServiceType(serviceName = "orgDataSchemeColumnService")
public class OrgDataSchemeColumnModel extends BaseDeleteModel {

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
	 * 字段类型。枚举：String，Integer，Boolean，Double
	 */
	@NotBlank(message = "字段类型不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 40, message = "字段类型最大长度不能超过40", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@EnumFiled(type = CommonConstant.SCHEME_COLUMN_TYPE.class, targetProperty = "columnTypeName")
	@TableField(value = "column_type")
	private String columnType;

	@TableField(exist = false)
	private String columnTypeName;

	/**
	 * 条件符号。枚举：=-等于，!=-不等于，>-大于，>=大于等于，<-小于，<=-小于等于
	 */
	@NotEmpty(message = "条件符号不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(exist = false)
	private List<String> conditionSymbolList;

	@TableField(exist = false)
	private List<String> conditionSymbolNameList;

	/**
	 * 以逗号分隔的条件符号字符串，用于列表显示
	 */
	@TableField(exist = false)
	private String conditionSymbolString;

	/**
	 * 条件内容。枚举：anyText-任意文本，@currentUserId-当前用户，@currentUnitId-当前单位，@currentDeptId-当前部门
	 */
	@NotBlank(message = "条件内容不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 40, message = "条件内容最大长度不能超过40", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@EnumFiled(type = CommonConstant.COLUMN_CONDITION_CONTENT.class, targetProperty = "conditionContentName")
	@TableField(value = "condition_content")
	private String conditionContent;

	@TableField(exist = false)
	private String conditionContentName;

	/**
	 * 备注
	 */
	@Length(max = 200, message = "备注最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "remark")
	private String remark;

	/**
	 * 菜单资源id
	 */
	@NotNull(message = "菜单资源id不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class, ValidatorQueryCheck.class})
	@TableField(value = "menu_resource_id")
	private Long menuResourceId;

}

package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import pro.wuan.common.core.annotation.EnumFiled;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.IDModel;
/**
 * 数据方案字段、条件符号关联表
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
@TableName("org_data_scheme_column_symbol")
@ServiceType(serviceName = "orgDataSchemeColumnSymbolService")
public class OrgDataSchemeColumnSymbolModel extends IDModel {

	private static final long serialVersionUID = 1L;

	/**
	 * 数据方案字段id
	 */
	@NotNull(message = "数据方案字段id不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "data_scheme_column_id")
	private Long dataSchemeColumnId;

	/**
	 * 条件符号。枚举：=-等于，!=-不等于，>-大于，>=大于等于，<-小于，<=-小于等于
	 */
	@NotBlank(message = "条件符号不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 40, message = "条件符号最大长度不能超过40", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@EnumFiled(type = CommonConstant.COLUMN_CONDITION_SYMBOL.class, targetProperty = "conditionSymbolName")
	@TableField(value = "condition_symbol")
	private String conditionSymbol;

	@TableField(exist = false)
	private String conditionSymbolName;

}

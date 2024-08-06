package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.model.IDModel;

/**
 * 元数据字段
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
@TableName("org_metadata_column")
@ServiceType(serviceName = "orgMetadataColumnService")
public class OrgMetadataColumnModel extends IDModel {

	private static final long serialVersionUID = 1L;

	/**
	 * 字段名称
	 */
	@TableField(value = "column_name")
	private String columnName;

	/**
	 * 字段注解
	 */
	@TableField(value = "column_code")
	private String columnCode;

	/**
	 * 字段类型（数据权限）。枚举：String，Int，Boolean，Double，Long
	 */
	@TableField(value = "column_type")
	private String columnType;

	/**
	 * 条件符号（数据权限）。枚举：=，!=，>，>=，<，<=
	 */
	@TableField(value = "condition_symbol")
	private String conditionSymbol;

	/**
	 * 条件内容（数据权限）
	 */
	@TableField(value = "condition_content")
	private String conditionContent;

	/**
	 * 显示状态。枚举：0-隐藏，1-显示
	 */
	@TableField(value = "status")
	private Integer status;

	/**
	 * 表单可写标识。枚举：0-不可写，1-可写
	 */
	@TableField(value = "form_edit_flag")
	private Integer formEditFlag;

	/**
	 * 临时状态
	 */
	@TableField(value = "tmp_status")
	private Integer tmpStatus;

	/**
	 * 备注
	 */
	@TableField(value = "remark")
	private String remark;

	/**
	 * 元数据id
	 */
	@TableField(value = "metadata_id")
	private Long metadataId;

}

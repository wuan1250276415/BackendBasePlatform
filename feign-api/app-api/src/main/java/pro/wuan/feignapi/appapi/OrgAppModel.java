package pro.wuan.feignapi.appapi;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pro.wuan.common.core.annotation.EnumFiled;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.BaseDeleteModel;


/**
 * 应用
 *
 * @author: liumin
 * @date: 2021-08-30 11:22:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("org_app")
@ServiceType(serviceName = "orgAppService")
public class OrgAppModel extends BaseDeleteModel {

	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@NotBlank(message = "名称不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 200, message = "名称最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "name")
	private String name;

	/**
	 * 编码
	 */
	@NotBlank(message = "编码不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 200, message = "编码最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "code")
	private String code;

	/**
	 * 类型。枚举：web-网页应用，app-手机应用，api-API应用
	 */
	@NotBlank(message = "类型不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 40, message = "类型最大长度不能超过40", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@EnumFiled(type = CommonConstant.APP_TYPE.class, targetProperty = "typeName")
	@TableField(value = "type")
	private String type;

	@TableField(exist = false)
	private String typeName;

	/**
	 * 图标
	 */
	@NotBlank(message = "图标不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 150, message = "图标最大长度不能超过150", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "icon")
	private String icon;

	/**
	 * 地址
	 */
	@Length(max = 200, message = "地址最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "url")
	private String url;

	/**
	 * 排序
	 */
	@Range(max = 10000, message = "排序最大值不能超过10000", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "sort")
	private Integer sort;

	/**
	 * 备注
	 */
	@Length(max = 200, message = "备注最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "remark")
	private String remark;

	/**
	 * 启用状态。枚举：0-停用，1-启用
	 */
	@NotNull(message = "启用状态不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Range(max = 1, message = "启用状态最大值不能超过1", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@EnumFiled(type = CommonConstant.USE_STATUS.class, targetProperty = "statusName")
	@TableField(value = "status")
	private Integer status;

	@TableField(exist = false)
	private String statusName;

}

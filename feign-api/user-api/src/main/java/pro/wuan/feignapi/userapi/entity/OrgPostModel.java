package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pro.wuan.common.core.annotation.ConvertMapping;
import pro.wuan.common.core.annotation.EnumFiled;
import pro.wuan.common.core.annotation.IDToString;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.BaseTenantModel;

import jakarta.validation.constraints.*;

/**
 * 岗位
 *
 * @author: liumin
 * @date: 2021-08-30 11:19:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("org_post")
@ServiceType(serviceName = "orgPostService")
public class OrgPostModel extends BaseTenantModel {

	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@NotBlank(message = "名称不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 200, message = "名称最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "name")
	private String name;

	/**
	 * 类型。枚举：diligent-工勤岗位，technology-专技岗位，manage-管理岗位
	 */
	@NotBlank(message = "类型不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 40, message = "类型最大长度不能超过40", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@EnumFiled(type = CommonConstant.POST_TYPE.class, targetProperty = "typeName")
	@TableField(value = "type")
	private String type;

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
	 * 部门id
	 */
	@NotNull(message = "所属部门不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@IDToString(type = OrgOrganModel.class, mappings = {@ConvertMapping(sourceProperty = "name", targetProperty = "deptName")})
	@TableField(value = "dept_id")
	private Long deptId;

	/**
	 * 部门名称
	 */
	@TableField(exist = false)
	private String deptName;

	/**
	 * 单位id
	 */
	@IDToString(type = OrgOrganModel.class, mappings = {@ConvertMapping(sourceProperty = "name", targetProperty = "unitName")})
	@TableField(value = "unit_id")
	private Long unitId;

	/**
	 * 单位名称
	 */
	@TableField(exist = false)
	private String unitName;

}

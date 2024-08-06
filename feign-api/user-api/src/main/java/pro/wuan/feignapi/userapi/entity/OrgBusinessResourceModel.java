package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
import pro.wuan.common.core.model.BaseDeleteModel;
import pro.wuan.feignapi.appapi.OrgAppModel;
import pro.wuan.feignapi.userapi.group.ValidatorFunctionSaveCheck;
import pro.wuan.feignapi.userapi.group.ValidatorFunctionUpdateCheck;
import pro.wuan.feignapi.userapi.group.ValidatorMenuSaveCheck;
import pro.wuan.feignapi.userapi.group.ValidatorMenuUpdateCheck;

import java.util.List;

/**
 * 业务资源
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
@TableName("org_business_resource")
@ServiceType(serviceName = "orgBusinessResourceService")
public class OrgBusinessResourceModel extends BaseDeleteModel {

	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@NotBlank(message = "名称不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@Length(max = 200, message = "名称最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "name")
	private String name;

	/**
	 * 类型。枚举：catalog-目录，menu-菜单，function-功能
	 */
	@NotBlank(message = "类型不能为空", groups = {ValidatorMenuSaveCheck.class, ValidatorMenuUpdateCheck.class})
	@Length(max = 40, message = "类型最大长度不能超过40", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@EnumFiled(type = CommonConstant.BUSINESS_RESOURCE_TYPE.class, targetProperty = "typeName")
	@TableField(value = "type")
	private String type;

	@TableField(exist = false)
	private String typeName;

	/**
	 * 编码（功能）
	 */
	@NotBlank(message = "编码不能为空", groups = {ValidatorFunctionSaveCheck.class, ValidatorFunctionUpdateCheck.class})
	@Length(max = 200, message = "编码最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "code")
	private String code;

	/**
	 * 图标（目录/菜单）
	 */
	@NotBlank(message = "图标不能为空", groups = {ValidatorMenuSaveCheck.class, ValidatorMenuUpdateCheck.class})
	@Length(max = 200, message = "图标最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "icon")
	private String icon;

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
	 * 地址
	 */
	@NotBlank(message = "地址不能为空", groups = {ValidatorMenuSaveCheck.class, ValidatorMenuUpdateCheck.class})
	@Length(max = 200, message = "地址最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@TableField(value = "url")
	private String url;

	/**
	 * 打开方式（菜单）。枚举：_self-当前页，_blank-空白页
	 */
	@Length(max = 40, message = "打开方式最大长度不能超过40", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@EnumFiled(type = CommonConstant.OPEN_MODE.class, targetProperty = "openModeName")
	@TableField(value = "open_mode")
	private String openMode;

	@TableField(exist = false)
	private String openModeName;

	/**
	 * 父id
	 */
	@NotNull(message = "上级不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
	@IDToString(type = OrgBusinessResourceModel.class, mappings = {@ConvertMapping(sourceProperty = "name", targetProperty = "parentName")})
	@TableField(value = "parent_id")
	private Long parentId;

	@TableField(exist = false)
	private String parentName;

	/**
	 * 应用id
	 */
	@NotNull(message = "应用不能为空", groups = {ValidatorMenuSaveCheck.class, ValidatorMenuUpdateCheck.class})
	@IDToString(type = OrgAppModel.class, mappings = {@ConvertMapping(sourceProperty = "name", targetProperty = "appName")})
	@TableField(value = "app_id")
	private Long appId;

	@TableField(exist = false)
	private String appName;

	/**
	 * 菜单资源id（功能）
	 */
	@TableField(value = "menu_resource_id")
	private Long menuResourceId;

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
	 * 下级业务资源列表
	 */
	@TableField(exist = false)
	private List<OrgBusinessResourceModel> children;

}

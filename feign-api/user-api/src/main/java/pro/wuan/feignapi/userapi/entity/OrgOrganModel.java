package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pro.wuan.common.core.annotation.DicFiled;
import pro.wuan.common.core.annotation.EnumFiled;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.BaseTenantModel;
import pro.wuan.feignapi.userapi.group.ValidatorUnitSaveCheck;
import pro.wuan.feignapi.userapi.group.ValidatorUnitUpdateCheck;

import jakarta.validation.constraints.*;

/**
 * 组织机构
 *
 * @author: liumin
 * @date: 2021-08-30 11:21:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("org_organ")
@ServiceType(serviceName = "orgOrganService")
public class OrgOrganModel extends BaseTenantModel {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @Length(max = 200, message = "名称最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "name")
    private String name;

    /**
     * 类型。枚举：unit-单位，dept-部门
     */
    @Length(max = 40, message = "类型最大长度不能超过40", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @EnumFiled(type = CommonConstant.ORGAN_TYPE.class, targetProperty = "typeName")
    @TableField(value = "type")
    private String type;

    @TableField(exist = false)
    private String typeName;

    /**
     * 机构业务类型。字典编码：cloud_organBusinessType
     */
    @NotBlank(message = "机构业务类型不能为空", groups = {ValidatorUnitSaveCheck.class, ValidatorUnitUpdateCheck.class})
    @Length(max = 10, message = "机构业务类型最大长度不能超过10", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @DicFiled(key = CommonConstant.DICT_KEY_ORGAN_BUSINESS_TYPE, name = "机构业务类型", targetProperty = "organBusinessTypeName")
    @TableField(value = "organ_business_type")
    private String organBusinessType;

    @TableField(exist = false)
    private String organBusinessTypeName;

    /**
     * 单位编号
     */
    @Length(max = 50, message = "单位编号最大长度不能超过50", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "unite_credit_code")
    private String uniteCreditCode;

    /**
     * 地址
     */
    @Length(max = 150, message = "地址最大长度不能超过150", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "address")
    private String address;

    /**
     * 法定代表人
     */
    @Length(max = 80, message = "法定代表人最大长度不能超过80", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "legal_person")
    private String legalPerson;

    /**
     * 联系人
     */
    @Length(max = 80, message = "联系人最大长度不能超过80", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "contact_person")
    private String contactPerson;

    /**
     * 电话
     */
    @Length(max = 30, message = "电话最大长度不能超过30", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "tel")
    private String tel;

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
     * 上级组织机构id
     */
    @NotNull(message = "上级不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 树行结构Ids
     */
    @TableField(value = "tree_ids")
    private String treeIds;

    /**
     * 部门负责人id
     */
    @TableField(value = "dept_head_id")
    private Long deptHeadId;

    /**
     * 垂管单位id
     */
    @NotNull(message = "垂管单位不能为空", groups = {ValidatorUnitSaveCheck.class, ValidatorUnitUpdateCheck.class})
    @TableField(value = "direct_unit_id")
    private Long directUnitId;

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

    /**
     * 单位管理员账号（单位）
     */
    @NotBlank(message = "单位管理员账号不能为空", groups = {ValidatorUnitSaveCheck.class, ValidatorUnitUpdateCheck.class})
    @Length(max = 100, message = "单位管理员账号最大长度不能超过100", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "unit_admin_account")
    private String unitAdminAccount;

    /**
     * 所在市
     */
    @TableField(value = "szs")
    private String szs;

    /**
     * 单位路径名
     */
    @TableField(exist = false)
    private String pathName;

}

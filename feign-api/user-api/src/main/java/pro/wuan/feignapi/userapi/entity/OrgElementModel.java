package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import pro.wuan.common.core.annotation.*;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateOneselfCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.BaseTenantModel;

import jakarta.validation.constraints.*;

/**
 * 元素
 *
 * @author: liumin
 * @date: 2021-08-30 11:24:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("org_element")
@ServiceType(serviceName = "orgElementService")
public class OrgElementModel extends BaseTenantModel {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 类型。枚举：unit-单位，dept-部门，post-岗位，user-用户，role-角色
     */
    @EnumFiled(type = CommonConstant.ELEMENT_TYPE.class, targetProperty = "typeName")
    @TableField(value = "type")
    private String type;

    @TableField(exist = false)
    private String typeName;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 父id
     */
    @IDToString(type = OrgOrganModel.class, mappings = {@ConvertMapping(sourceProperty = "name", targetProperty = "unitName")})
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 父名称
     */
    @TableField(exist = false)
    private String parentName;

    /**
     * 树形结构ids
     */
    @TableField(value = "tree_ids")
    private String treeIds;

    /**
     * 启用状态。枚举：0-停用，1-启用
     */
    @EnumFiled(type = CommonConstant.USE_STATUS.class, targetProperty = "statusName")
    @TableField(value = "status")
    private Integer status;

    @TableField(exist = false)
    private String statusName;

    /**
     * 是否存在下级元素
     */
    @TableField(exist = false)
    private boolean haveChild;

    @TableField(exist = false)
    private String unitName;

    @DicFiled(key = "rylx", name = "人员类型", targetProperty = "userTypeName")
    @TableField(exist = false)
    private String userType;
    @TableField(exist = false)
    private String userTypeName;

    @DicFiled(key = "ryzw", name = "人员职务", targetProperty = "zwName")
    @TableField(exist = false)
    private String zw;
    @TableField(exist = false)
    private String zwName;

    @DicFiled(key = "bdjx", name = "部队军衔", targetProperty = "jxName")
    @TableField(exist = false)
    private String jx;
    @TableField(exist = false)
    private String jxName;

    /**
     * 军证号
     */
    @TableField(exist = false)
    private String attrString02;

    /**
     * 性别。枚举：man-男，woman-女
     */
    @EnumFiled(type = CommonConstant.SEX.class, targetProperty = "sexName")
    @TableField(exist = false)
    private String sex;

    @TableField(exist = false)
    private String sexName;

    /**
     * 身份证号
     */
    @TableField(exist = false)
    private String idCardNo;

    /**
     * 头像url
     */
    @Length(max = 180, message = "头像url最大长度不能超过180", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(exist = false)
    private String headImageUrl;

    /**
     * 手机号
     */
    @Length(max = 30, message = "电话最大长度不能超过30", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class, ValidatorUpdateOneselfCheck.class})
    @TableField(exist = false)
    private String tel;



    /**
     * 组织机构id
     */
    @NotNull(message = "所属部门不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @IDToString(type = OrgOrganModel.class, mappings = {@ConvertMapping(sourceProperty = "name", targetProperty = "organName")})
    @TableField(exist = false)
    private Long organId;

    /**
     * 组织机构名称
     */
    @TableField(exist = false)
    private String organName;

}

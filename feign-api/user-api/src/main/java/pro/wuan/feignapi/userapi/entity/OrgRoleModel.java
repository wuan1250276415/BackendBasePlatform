package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.model.BaseTenantModel;

import jakarta.validation.constraints.*;

/**
 * 角色
 *
 * @author: liumin
 * @date: 2021-08-30 11:15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("org_role")
@ServiceType(serviceName = "orgRoleService")
public class OrgRoleModel extends BaseTenantModel {

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
    @Length(max = 200, message = "编码最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "code")
    private String code;

    /**
     * 类型。枚举：system-系统角色，business-业务角色
     */
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
     * 应用id
     */
    @NotNull(message = "应用id不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "app_id")
    private Long appId;

    /**
     * 组织机构id
     */
    @NotNull(message = "组织机构id不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "organ_id")
    private Long organId;

    /**
     * 单位ID串
     */
    @TableField(exist = false)
    private String unitTreeIds;

}

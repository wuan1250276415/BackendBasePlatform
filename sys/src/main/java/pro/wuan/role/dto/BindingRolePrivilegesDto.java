package pro.wuan.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 绑定单位角色权限dto
 */
@Data
public class BindingRolePrivilegesDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @Length(max = 200, message = "名称最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private String name;

    /**
     * 编码
     */
    @Length(max = 200, message = "编码最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private String code;

    /**
     * 类型。枚举：system-系统角色，business-业务角色
     */
    private String type;

    /**
     * 排序
     */
    @Range(max = 10000, message = "排序最大值不能超过10000", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private Integer sort;

    /**
     * 备注
     */
    @Length(max = 200, message = "备注最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private String remark;

    /**
     * 应用id
     */
    @NotNull(message = "应用id不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private Long appId;

    /**
     * 组织机构id
     */
    @NotNull(message = "组织机构id不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private String organIds;

    /**
     * 菜单权限id列表
     */
    private List<Long> menuIdList;

    /**
     * 功能权限id列表（按钮）
     */
    private List<Long> functionIdList;

    /**
     * 列表权限id列表
     */
    private List<Long> listIdList;

    /**
     * 数据权限id列表
     */
    private List<Long> dataIdList;

}

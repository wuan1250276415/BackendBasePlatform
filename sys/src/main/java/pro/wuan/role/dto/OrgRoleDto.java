package pro.wuan.role.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.model.BaseTenantModel;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色
 *
 * @author: liumin
 * @date: 2021-08-30 11:15:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgRoleDto extends BaseTenantModel {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 多角色名称接值
     */
    private List<String> roleNameList = new ArrayList<>();

    /**
     * 多组织机构id接值
     */
    @NotNull(message = "组织机构id不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private String organIds;


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
     * 备注
     */
    @Length(max = 200, message = "备注最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private String remark;

    /**
     * 应用id
     */
    @NotNull(message = "应用id不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private Long appId;

}

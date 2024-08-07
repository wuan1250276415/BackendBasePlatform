package pro.wuan.role.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * （新）导入角色excel实体类
 */
@Data
public class NewExcelRoleDto implements Serializable {

    public static final long serialVersionUID = 1L;

    @Excel(name = "所属单位(必填)")
    @NotBlank(message = "所属单位不能空")
    private String unitName;

    @Excel(name = "角色名(必填)")
    @NotBlank(message = "角色名不能空")
    private String name;

    @Excel(name = "备注")
    private String remark;

}

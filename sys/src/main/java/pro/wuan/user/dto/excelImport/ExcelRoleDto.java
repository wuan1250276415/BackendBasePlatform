package pro.wuan.user.dto.excelImport;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 导入角色excel实体类
 */
@Data
public class ExcelRoleDto implements Serializable {

    public static final long serialVersionUID = 1L;

    @Excel(name = "角色名称(必填)")
    @NotBlank(message = "角色名称不能空")
    private String name;

    @Excel(name = "所属单位名称(必填)")
    @NotBlank(message = "所属单位名称不能空")
    private String unitName;

    @Excel(name = "备注")
    private String remark;

}

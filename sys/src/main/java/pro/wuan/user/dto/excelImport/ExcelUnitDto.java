package pro.wuan.user.dto.excelImport;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 导入单位excel实体类
 */
@Data
public class ExcelUnitDto implements Serializable {

    public static final long serialVersionUID = 1L;

    @Excel(name = "单位名称(必填)")
    @NotBlank(message = "单位名称不能空")
    private String name;

//    @Excel(name = "单位编号")
//    private String uniteCreditCode;

//    @Excel(name = "单位类型")
//    @NotBlank(message = "单位类型不能空")
//    private String organBusinessType;

    @Excel(name = "单位管理员账号(必填)")
    @NotBlank(message = "单位管理员账号不能空")
    private String unitAdminAccount;

    @Excel(name = "上级单位名称(必填)")
    private String parentName;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "单位类型(必填)")
    @NotBlank(message = "单位类型不能空")
    private String organBusinessType;


}

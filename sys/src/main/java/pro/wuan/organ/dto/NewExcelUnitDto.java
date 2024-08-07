package pro.wuan.organ.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * （新）导入单位excel实体类
 */
@Data
public class NewExcelUnitDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "单位名称")
    @NotBlank(message = "单位名称不能空")
    private String name;

//    @Excel(name = "单位编号")
//    private String uniteCreditCode;

//    @Excel(name = "单位类型")
//    @NotBlank(message = "单位类型不能空")
//    private String organBusinessType;

    @Excel(name = "单位管理员账号")
    @NotBlank(message = "单位管理员账号不能空")
    private String unitAdminAccount;

    @Excel(name = "上级单位名称")
    private String parentName;

    @Excel(name = "备注")
    private String remark;

}

package pro.wuan.user.dto.excelImport;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 导入用户excel实体类
 */
@Data
public class ExcelUserDto implements Serializable {

    public static final long serialVersionUID = 1L;

    @Excel(name = "姓名(必填)")
    @NotBlank(message = "姓名不能空")
    private String name;

    @Excel(name = "性别")
    @Pattern(regexp = "^[男|女]$", message = "性别只能输入男/女")
    @NotBlank(message = "性别不能空")
    private String sex;

    @Excel(name = "登录账号(必填)")
//    @NotBlank(message = "登录账号不能空")
    private String loginName;

    @Excel(name = "所属单位名称(必填)")
    @NotBlank(message = "所属单位名称不能空")
    private String unitName;

    @Excel(name = "关联角色名称(必填)")
    @NotBlank(message = "关联角色名称不能空")
    private String roleName;

    @Excel(name = "人脸图像", type = 2)
    private String image;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "身份证号")
    private String sfzh;

    @Excel(name = "军证号")
    private String jzh;

    @Excel(name = "职务")
    private String zw;

    @Excel(name = "军衔")
    private String jx;

    @Excel(name = "人员类型")
    private String rylx;


}

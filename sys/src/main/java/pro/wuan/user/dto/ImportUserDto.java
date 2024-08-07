package pro.wuan.user.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 导入用户excel实体类
 */
@Data
public class ImportUserDto implements Serializable {

    public static final long serialVersionUID = 1L;

    @Excel(name = "姓名")
    @NotBlank(message = "姓名不能空")
    private String name;

    @Excel(name = "性别")
    @Pattern(regexp = "^[男|女]$", message = "性别只能输入男/女")
    @NotBlank(message = "性别不能空")
    private String sex;

    @Excel(name = "身份证号码")
    private String idCardNo;

    @Excel(name = "工作证号")
    @NotBlank(message = "工作证号不能空")
    private String loginName;

    @Excel(name = "手机号码")
    @NotBlank(message = "手机号码不能空")
    private String tel;

    @Excel(name = "备注")
    private String remark;

}

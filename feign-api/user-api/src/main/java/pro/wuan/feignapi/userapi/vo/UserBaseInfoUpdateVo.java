package pro.wuan.feignapi.userapi.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.*;

/**
 * 用户基本信息更新vo
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
@Data
public class UserBaseInfoUpdateVo {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Length(max = 205, message = "用户名最大长度不能超过205")
    private String userName;

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Length(max = 30, message = "新密码最大长度不能超过30")
    private String newPassword;

    /**
     * 启用状态。枚举：0-停用，1-启用
     */
    @NotNull(message = "启用状态不能为空")
    @Range(max = 1, message = "启用状态最大值不能超过1")
    private Integer status;

}

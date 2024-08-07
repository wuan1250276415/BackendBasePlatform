package pro.wuan.user.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Data
public class UserPasswordDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 用户id
     */
    @NotNull
    private Long userId;


    /**
     * 旧密码
     */
    @NotNull
    private String oldPassword;

    /**

     * 新密码
     */
    @NotNull
    private String newPassword;


    /**
     * 密码强度 1弱；2中；3强
     */
    @NotNull
    private String passwordStrength;
}

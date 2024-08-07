package pro.wuan.user.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pro.wuan.common.core.model.BaseDeleteModel;

import java.util.Date;

//系统验证码
@TableName(value = "sys_captcha")
@Data
public class SysCaptchaEntity extends BaseDeleteModel {


    //前端传的随机码
    private String uuid;


    //验证码
    private String code;


    //过期时间
    private Date expireTime;


}

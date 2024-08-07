/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package pro.wuan.user.entity;

import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 登录表单
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
public class SysLoginForm {

    /**
     *登录名
     */
    @NotBlank(message = "登录名不能为空！")
    private String loginName;


    /**
     *密码
     */
    @NotBlank(message = "密码不能为空！")
    private String password;


    /**
     *验证码
     * @required
     */
    @NotBlank(message = "验证码不能为空！")
    private String captcha;


    /**
     *前端随机码
     * @required
     */
    @NotBlank(message = "随机码不能为空！")
    private String uuid;



}

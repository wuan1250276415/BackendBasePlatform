package pro.wuan.user.controller;


import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.wuan.common.auth.util.JwtUtils;
import pro.wuan.common.core.constant.SecurityConstants;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.UserSet;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.user.entity.SysLoginForm;
import pro.wuan.user.mapper.UserSetMapper;
import pro.wuan.user.service.IOrgUserLoginStatusService;
import pro.wuan.user.service.IOrgUserService;
import pro.wuan.user.service.ISysCaptchaService;
import pro.wuan.user.service.IUserSetService;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 登录
 * @program: tellhowcloud
 * @author zf
 * @create 2021-08-30 11:15:00
 */
@RestController
public class LoginController extends CURDController<IUserSetService, UserSetMapper, UserSet> {



    @Autowired
    private ISysCaptchaService sysCaptchaService;
    @Autowired
    private IOrgUserService orgUserService;
    @Autowired
    private IOrgUserLoginStatusService orgUserLoginStatusService;




    /**
     * 获取验证码
     * @param uuid 前端生成的随机码
     * @throws IOException
     */
    @RequestMapping(value = "/captcha.jpg", method = RequestMethod.GET)
    public void captcha(HttpServletResponse response, @NotNull String uuid)throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        //获取图片验证码
        BufferedImage image = sysCaptchaService.getCaptcha(uuid);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        IOUtils.closeQuietly(out);
    }





    /**
     *用户登录
     * @param form 封装用户登录参数
     * @return 返回用户详情
     */
    //@AspectLog(description = "用户登录", type = OperationType.OPERATION_LOGIN)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<UserDetails> login(HttpServletRequest request, HttpServletResponse response, @Validated @RequestBody SysLoginForm form) {
        try {
            //校验验证码是否正确
            boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
            if(!captcha){
                return Result.failure("验证码不正确或已过期");
            }
            Result<UserDetails> result=orgUserService.login(form.getLoginName(),form.getPassword());
            this.handleResult(response,result);
            if (result.isSuccess()) {
                orgUserLoginStatusService.loginOrgUserLoginStatus(request,result.getResult().getUser().getId());
            }
            return result;
        } catch (Exception e) {
            return Result.failure(StrUtil.format("登录失败，异常如下：{}", ExceptionUtil.getMessage(e)));
        }
    }



    /**
     *人脸识别登录
     * @return 返回用户详情
     */
    @RequestMapping(value = "/faceLogin", method = RequestMethod.POST)
    public Result<UserDetails> faceLogin(HttpServletResponse response,@NotBlank MultipartFile file) {
        try {
            Result<UserDetails> result=orgUserService.faceLogin(file);
            this.handleResult(response,result);
            return result;
        } catch (Exception e) {
            return Result.failure(StrUtil.format("登录失败，异常如下：{}", ExceptionUtil.getMessage(e)));
        }
    }





    public void handleResult(HttpServletResponse response, Result<UserDetails> result) {
        //登录成功
        if (result.isSuccess()) {
            UserDetails userDetails = result.getResult();
            //请求响应头中设置token
            response.setHeader(SecurityConstants.REQUEST_AUTH_HEADER, userDetails.getToken());
            //防止token从body带出
            userDetails.setToken(userDetails.getToken());
        }
    }




    /**
     * 登出
     * @return
     */
    //@AspectLog(description = "用户登出", type = OperationType.OPERATION_LOGOUT)
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        try {
            JwtUtils.setTokenInvalid(UserContext.getCurrentUser().getAccount());
            orgUserLoginStatusService.loginOutOrgUserLoginStatus(request,UserContext.getCurrentUserId());
            return Result.success();
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }


    /**
     * 锁屏
     * @return
     */
    //@AspectLog(description = "用户锁屏", type = OperationType.OPERATION_UPDATE)
    @RequestMapping(value = "/lockScreen", method = RequestMethod.POST)
    public Result lockScreen() {
        try {
            return orgUserService.lockScreen();
        } catch (Exception e) {
            return Result.failure(StrUtil.format("锁屏失败，异常如下：{}", ExceptionUtil.getMessage(e)));
        }
    }




    /**
     * 解锁
     * @param password 密码(MD5加密)
     * @return
     */
    //@AspectLog(description = "用户解锁", type = OperationType.OPERATION_UPDATE)
    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    public Result unlock(@NotNull String password) {
        try {
            return orgUserService.unlock(password);
        } catch (Exception e) {
            return Result.failure(StrUtil.format("解锁失败，异常如下：{}", ExceptionUtil.getMessage(e)));
        }
    }



    /**
     * 选择应用
     *
     * @param appCode 应用编码
     * @return 用户详情
     */
    @GetMapping("/selectApp")
    public Result<UserDetails> selectApp(@NotBlank(message = "appCode不能为空") String appCode) {
        try {
            return orgUserService.selectApp(appCode);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }


}

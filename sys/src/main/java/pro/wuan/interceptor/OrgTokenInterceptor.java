package pro.wuan.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import pro.wuan.common.auth.util.JwtUtils;
import pro.wuan.common.auth.util.LicenseUtil;
import pro.wuan.common.auth.util.TokenUtil;
import pro.wuan.common.core.constant.SecurityConstants;
import pro.wuan.common.core.model.BaseUser;
import pro.wuan.common.core.model.GlobalContext;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.user.service.IOrgUserService;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @description: token拦截器
 * @author: oldone
 * @date: 2020/8/5 10:47
 */
@Slf4j
@Configuration
public class OrgTokenInterceptor implements HandlerInterceptor {
    @Autowired
    private IOrgUserService orgUserService;
    @Autowired
    TokenUtil tokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("request url:" + request.getRequestURI());
        response.setHeader("Access-Control-Allow-Origin", "*");//设置允许哪些域名应用进行ajax访问
        response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
        response.setHeader("Access-Control-Allow-Headers", "*");

        //判断授权是否合法
        String result=LicenseUtil.checkLicense("");
        if (!result.equals("success")) {
        }

        String token = request.getHeader(SecurityConstants.REQUEST_AUTH_HEADER);
        log.info("request token:" + token);
        if (StringUtils.isEmpty(token) || "null".equals(token)) {
            log.warn("token is empty");
            return true;
        }
        //线程变量存储用户信息
        String account = JwtUtils.getClaim(token, SecurityConstants.ACCOUNT);
        UserDetails userDetails = orgUserService.selectByLoginName(account);
        log.info("request user:{}", JSON.toJSONString(userDetails));
        userDetails.setToken(token);
        UserContext.setCurrentUser(userDetails);
        //通用线程变量
        BaseUser baseUser = new BaseUser();
        baseUser.setUserId(UserContext.getCurrentUserId());
        baseUser.setUserName(UserContext.getCurrentUserName());
        baseUser.setTenantId(UserContext.getCurrentUserTenantId());
        baseUser.setOrganId(UserContext.getCurrentOrganId());
        GlobalContext.setBaseUser(baseUser);
        return true;
    }

}

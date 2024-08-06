package pro.wuan.feignapi.userapi.interceptor;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.HandlerInterceptor;
import pro.wuan.common.auth.util.JwtUtils;
import pro.wuan.common.auth.util.TokenUtil;
import pro.wuan.common.core.constant.HttpStatus;
import pro.wuan.common.core.constant.SecurityConstants;
import pro.wuan.common.core.model.BaseUser;
import pro.wuan.common.core.model.GlobalContext;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.config.SqlDefualtAdditionalConfiguration;
import pro.wuan.feignapi.userapi.feign.UserFeignClient;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.model.UserDetails;

import java.io.IOException;

/**
 * @description: token拦截器
 * @author: oldone
 * @date: 2020/8/5 10:47
 */
@Slf4j
@Configuration
public class TokenInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    TokenUtil tokenUtil;
    @Lazy
    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    private SqlDefualtAdditionalConfiguration additionalConfiguration;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");//设置允许哪些域名应用进行ajax访问
        response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
        response.setHeader("Access-Control-Allow-Headers", "*");
        String token = request.getHeader(SecurityConstants.REQUEST_AUTH_HEADER);
        if (StringUtils.isEmpty(token)) {
            log.warn("token is empty");
//            return authErro(response, "令牌不能为空，请登录!", HttpStatus.UNAUTHORIZED);
            return true;
        }
        //线程变量存储用户信息
        String account = JwtUtils.getClaim(token, SecurityConstants.ACCOUNT);
        UserDetails userDetails = userFeignClient.selectByLoginName(account);
        userDetails.setToken(token);
        UserContext.setCurrentUser(userDetails);
        //通用线程变量
        BaseUser baseUser = new BaseUser();
        baseUser.setUserId(UserContext.getCurrentUserId());
        baseUser.setUserName(UserContext.getCurrentUserName());
        baseUser.setTenantId(UserContext.getCurrentUserTenantId());
        baseUser.setOrganId(UserContext.getCurrentOrganId());
        baseUser.setIsSuperAdmin(UserContext.isSuperAdmin());
        if (additionalConfiguration.getIsEnable() && !UserContext.isSuperAdmin()) {
            //非超管用户
            Long organId = userDetails.getUser().getOrganId();
            String databaseModl = userFeignClient.getOrganDabaseModel(organId);
            if (StrUtil.isBlank(databaseModl)) {
                return authErro(response, "用户所属数据库模式不能为空!", HttpStatus.FORBIDDEN);
            }
            //存储用户所属数据库模式
            baseUser.setDatabaseModl(databaseModl);
        }
        GlobalContext.setBaseUser(baseUser);
        return true;
    }

    /**
     * 认证错误输出
     *
     * @param response 响应对象
     * @param mess     错误信息
     * @return
     */
    private boolean authErro(HttpServletResponse response, String mess, HttpStatus status) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(status.value());
        String returnStr = "";
        try {
            //设置响应内容
            Result result = new Result();
            result.setCode(status.value());
            result.setMessage(mess);
            returnStr = objectMapper.writeValueAsString(result);
//                response.getWriter().print(result);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }

        response.getWriter().write(returnStr);
        return false;
    }
}

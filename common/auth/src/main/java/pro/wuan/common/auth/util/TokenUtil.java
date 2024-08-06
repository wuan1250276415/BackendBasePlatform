package pro.wuan.common.auth.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pro.wuan.common.core.constant.SecurityConstants;
import pro.wuan.common.core.model.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @description:
 * @author: oldone
 * @date: 2020/11/5 16:52
 */
@Slf4j
@Component
public class TokenUtil {
    /**
     * 检查token
     * @param request
     * @param response
     * @return
     */
    public boolean checkToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(SecurityConstants.REQUEST_AUTH_HEADER);
        String from = request.getHeader(SecurityConstants.REQUEST_FROM);
        //判断token是否为空
        if (StrUtil.isBlank(token)) {
            log.warn("orgTokenInterceptor error token is empty,url:"+request.getRequestURI());
            this.response401(response, "token is empty!");
            return false;
        }
        try {
            //token校验
            if (JwtUtils.checkToken(token)) {
                //判断是否feign请求
                if (!SecurityConstants.FROM_FEIGN.equals(from)) {
                    //非feign请求需要刷新token
                    //检查是否需要刷新token
                    String newToken = JwtUtils.refreshTokenIfNeed(token);
                    //判断是否产生了新token
                    if (StringUtils.isNotEmpty(newToken)) {
                        //响应带回新的token
                        response.setHeader(SecurityConstants.REQUEST_AUTH_HEADER, newToken);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.response401(response, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 401非法请求
     *
     * @param response
     * @param msg
     */
    public void response401(HttpServletResponse response, String msg) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();

            Result result = new Result();
            result.setCode(HttpStatus.UNAUTHORIZED.value());
            result.setMessage(msg);
            out.append(JSON.toJSONString(result));
        } catch (IOException e) {
            log.error("返回Response信息出现IOException异常:" + e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}

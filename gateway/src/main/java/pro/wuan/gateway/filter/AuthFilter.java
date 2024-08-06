package pro.wuan.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import pro.wuan.common.auth.AuthProperties;
import pro.wuan.common.auth.util.AuthUtils;
import pro.wuan.common.auth.util.JwtUtils;
import pro.wuan.common.auth.util.LicenseUtil;
import pro.wuan.common.core.constant.SecurityConstants;
import pro.wuan.common.core.exception.JwtException;
import pro.wuan.common.core.model.Result;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * @description: jwt过滤器
 * @author: oldone
 * @date: 2020/7/31 16:29
 */
@Slf4j
@Component
@ConfigurationProperties("tellhow.auth")
public class AuthFilter implements GlobalFilter, Ordered {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper;
    @Resource
    private AuthProperties authProperties;
//    @Autowired
//    private NacosConfigLocalCatch nacosConfigLocalCatch;

    public AuthFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 过滤器
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("enter authFilter...");
        ServerHttpResponse resp = exchange.getResponse();

        //判断是否开启license校验
        if (authProperties.getIsLicenseEnable()) {
            //开启模式
            //从授权文件读取授权码
            String license = this.readLicense();
            if (Strings.isEmpty(license)) {
                return authErro(resp, "没有授权码License，请联系管理员");
            }
            String checkResult = LicenseUtil.checkLicense(license);
            if (!"success".equals(checkResult)) {
                return authErro(resp, checkResult);
            }
        }
        //获取token
        String token = exchange.getRequest().getHeaders().getFirst(SecurityConstants.REQUEST_AUTH_HEADER);
        //是否有token
        boolean hasToken = StringUtils.isNotEmpty(token);
        //获取请求地址
        String url = exchange.getRequest().getURI().getPath();
        log.info("获取到的url地址:{}", url);
        log.info("获取到的token:{}", token);
        //判断是否忽略token认证地址
        if (!hasToken && !isSkip(url)) {
            log.info("过滤uri,{}", url);
            return chain.filter(exchange);
        }


        if (!hasToken) {
            //没有token
            return authErro(resp, "令牌不能为空，请登录!");
        } else {
            resp.getHeaders().set(SecurityConstants.REQUEST_AUTH_HEADER, token);
            //有token
            try {
                //token校验
                if (JwtUtils.checkToken(token)) {
                    //检查是否需要刷新token
                    String newToken = JwtUtils.refreshTokenIfNeed(token);
                    //判断是否产生了新token
                    if (StringUtils.isNotEmpty(newToken)) {
                        //响应带回新的token
                        resp.getHeaders().set(SecurityConstants.REQUEST_AUTH_HEADER, newToken);
                        //组装请求头参数
                        Consumer<HttpHeaders> httpHeaders = httpHeader -> {
                            httpHeader.set(SecurityConstants.REQUEST_AUTH_HEADER, newToken);
                        };
                        //产生了新token，则重新构建httprequest，设置请求头中的参数，原httprequest的请求头是只读的
                        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders).build();
                        exchange.mutate().request(serverHttpRequest).build();
                    }
                }

                //判断请求地址是否需要鉴权
                if (authProperties.getIsEnable() != null && authProperties.getIsEnable() && !isAuthSkip(url)) {
                    //从token中获取用户信息
                    String account = JwtUtils.getClaim(token, SecurityConstants.ACCOUNT);
                    //判断资源访问权限
                    if (!AuthUtils.isPermit(account, url)) {
                        return authErro(resp, "没有访问权限!");
                    }
                }
                return chain.filter(exchange);
            } catch (JwtException e) {
                //jwt异常处理
                log.error(e.getMessage(), e);
                return authErro(resp, ExceptionUtils.getMessage(e));
            } catch (Exception e) {
                //其他异常情况处理
                log.error(e.getMessage(), e);
                return authErro(resp, "令牌认证失败");
            }
        }

    }

    /**
     * 判断是否忽略token认证请求地址
     *
     * @param path 请求地址
     * @return
     */
    private boolean isSkip(String path) {
        return authProperties.getSkipUrl().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    /**
     * 判断是否忽略鉴权请求地址
     *
     * @param path 请求地址
     * @return
     */
    private boolean isAuthSkip(String path) {
        return authProperties.getSkipAuthUrl().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    /**
     * 认证错误输出
     *
     * @param resp 响应对象
     * @param mess 错误信息
     * @return
     */
    private Mono<Void> authErro(ServerHttpResponse resp, String mess) {
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String returnStr = "";
        try {
            //设置响应内容
            Result result = new Result();
            result.setCode(HttpStatus.UNAUTHORIZED.value());
            result.setMessage(mess);
            returnStr = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        DataBuffer buffer = resp.bufferFactory().wrap(returnStr.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Flux.just(buffer));
    }

    /**
     * 过滤器优先级别,数字越低越优先
     *
     * @return
     */
    @Override
    public int getOrder() {
        return -100;
    }


    /**
     * 读取授权文件内容
     *
     * @return
     */
    public String readLicense() {
        if (Strings.isEmpty(authProperties.getLicensePath())) {
            return Strings.EMPTY;
        }

        try {
            File file = new File(authProperties.getLicensePath());
            StringBuilder result = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                result.append(System.lineSeparator() + s);
            }
            return result.toString();
        } catch (Exception e) {
            log.error("readLicense error:" + e.getMessage());
        }
        return Strings.EMPTY;
    }
}

package pro.wuan.common.cloud.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

/**
 * feign接口调用拦截器，用于传递上游参数
 * 例:token等信息
 *
 * @description:
 * @author: oldone
 * @date: 2021/8/18 17:15
 */
public class FeignRequestConfig implements RequestInterceptor {

    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String values = request.getHeader(name);
                    requestTemplate.header(name, values);
                }
            }
        }
    }
}

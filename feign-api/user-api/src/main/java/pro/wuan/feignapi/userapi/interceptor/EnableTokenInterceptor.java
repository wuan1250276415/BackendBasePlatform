package pro.wuan.feignapi.userapi.interceptor;

import org.springframework.context.annotation.Import;
import pro.wuan.feignapi.userapi.config.TokenConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 开启token拦截器
 * @author: oldone
 * @date: 2020/8/5 11:21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({TokenConfiguration.class})
public @interface EnableTokenInterceptor {
}

package pro.wuan.feignapi.userapi.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pro.wuan.feignapi.userapi.interceptor.TokenInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: token拦截器配置
 * @author: oldone
 * @date: 2020/8/5 11:10
 */
@ConfigurationProperties("tellhow.auth")
@Data
public class TokenConfiguration implements WebMvcConfigurer {
    /**
     * 需进行token校验路径
     */
    private String[] addPathPatterns;
    /**
     * 无需token校验路径
     */
    private List<String> skipUrl = new ArrayList<>();
    /**
     * token拦截器
     */
    @Autowired
    TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 增加基础拦截器
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns(addPathPatterns)
                .excludePathPatterns(skipUrl.toArray(new String[0]));

    }
}

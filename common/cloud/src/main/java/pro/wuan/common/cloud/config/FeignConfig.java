package pro.wuan.common.cloud.config;

import feign.Request;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: oldone
 * @date: 2020/12/31 14:41
 */
//@Configuration
public class FeignConfig {
    /**
     * 设置请求超时时间
     * 默认
     * public Options() {
     * this(10 * 1000, 60 * 1000);
     * }
     */
    @Bean
    Request.Options feignOptions() {
        return new Request.Options(60 * 1000, TimeUnit.MICROSECONDS, 60 * 1000, TimeUnit.MICROSECONDS,true);
    }

}

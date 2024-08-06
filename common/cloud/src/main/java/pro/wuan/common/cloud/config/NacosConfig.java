package pro.wuan.common.cloud.config;

import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 解决微服务部署到tomcat不能向nacos注册问题
 *
 * @author: oldone
 * @date: 2020/12/17 10:26
 */
@Slf4j
@Component
public class NacosConfig implements ApplicationRunner {
    @Autowired(required = false)
    private NacosAutoServiceRegistration registration;

//    @Value("${server.port}")
//    Integer port;

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (registration != null) {
                registration.start();
            } else {
                log.info("NacosConfig registration is null");
            }
        } catch (Exception e) {
            log.error("NacosConfig error:", e);
        }
    }
}

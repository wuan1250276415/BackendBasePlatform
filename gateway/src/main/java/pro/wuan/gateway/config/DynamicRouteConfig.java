package pro.wuan.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.wuan.gateway.route.NacosRouteDefinitionRepository;

/**
 * 动态路由配置
 */
@Configuration
@ConditionalOnProperty(prefix = "gateway.dynamicRoute", name = "enabled", havingValue = "true")
public class DynamicRouteConfig  {

    @Resource
    private NacosConfigProperties nacosConfigProperties;

    @Bean
    public NacosRouteDefinitionRepository nacosRouteDefinitionRepository() {
        return new NacosRouteDefinitionRepository(nacosConfigProperties);
    }
}

package pro.wuan.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.wuan.gateway.route.NacosRouteDefinitionRepository;

/**
 * 动态路由配置
 */
@Configuration
@ConditionalOnProperty(prefix = "tellhow.gateway.dynamicRoute", name = "enabled", havingValue = "true")
public class DynamicRouteConfig  {
    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * Nacos实现方式
     */
    @Configuration
    @ConditionalOnProperty(prefix = "tellhow.gateway.dynamicRoute", name = "dataType", havingValue = "nacos", matchIfMissing = true)
    public class NacosDynRoute {
        @Autowired
        private NacosConfigProperties nacosConfigProperties;

        @Bean
        public NacosRouteDefinitionRepository nacosRouteDefinitionRepository() {
            return new NacosRouteDefinitionRepository(publisher, nacosConfigProperties);
        }
    }
}

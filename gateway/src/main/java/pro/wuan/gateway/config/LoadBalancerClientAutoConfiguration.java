package pro.wuan.gateway.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;
import pro.wuan.gateway.filter.LoadBalancerClientFilter;

/**
 * @description:
 * @author: oldone
 * @date: 2021/8/18 16:29
 */

@Configuration
@ConditionalOnClass({LoadBalancerClient.class, RibbonAutoConfiguration.class, DispatcherHandler.class})
@AutoConfigureAfter(RibbonAutoConfiguration.class)
public class LoadBalancerClientAutoConfiguration {

    @Bean
    @ConditionalOnBean(LoadBalancerClient.class)
    public LoadBalancerClientFilter loadBalancerClientFilter(LoadBalancerClient client) {
        return new LoadBalancerClientFilter(client);
    }
}

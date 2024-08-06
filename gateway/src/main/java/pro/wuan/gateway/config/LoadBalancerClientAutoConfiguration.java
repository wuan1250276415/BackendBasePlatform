package pro.wuan.gateway.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.loadbalancer.NacosLoadBalancer;
import jakarta.annotation.Resource;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @description:
 * @author: oldone
 * @date: 2021/8/18 16:29
 */

public class LoadBalancerClientAutoConfiguration {

    // 注入当前服务的nacos的配置信息
    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    @Bean
    ReactorLoadBalancer<ServiceInstance> nacosLoadBalancer(Environment environment,
                                                           LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new NacosLoadBalancer(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name, nacosDiscoveryProperties);
    }
}

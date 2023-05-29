package pro.wuan.core.config.loadblance;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestTransformer;
import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.util.concurrent.ThreadLocalRandom;

public class CustomLoadBalancerConfiguration {

    @Bean
    ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment environment,
                                                            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RandomLoadBalancer(loadBalancerClientFactory
                .getLazyProvider(name, ServiceInstanceListSupplier.class),
                name);
    }

    @Bean
    public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
            ConfigurableApplicationContext context) {
        return ServiceInstanceListSupplier.builder()
                .withDiscoveryClient()
                .withWeighted(instance -> ThreadLocalRandom.current().nextInt(1, 101)) // 权重
                .withCaching()  // 缓存服务实例
                .withHealthChecks() // 实例的健康检查
                .withSameInstancePreference()   //优先使用同一个实例
                .withHints() // 提示
                .build(context);
    }

    @Bean
    public LoadBalancerRequestTransformer transformer() {
        return new LoadBalancerRequestTransformer() {
            @Override
            public HttpRequest transformRequest(HttpRequest request, ServiceInstance instance) {
                return new HttpRequestWrapper(request) {
                    @Override
                    public HttpHeaders getHeaders() {
                        HttpHeaders headers = new HttpHeaders();
                        headers.putAll(super.getHeaders());
                        headers.add("X-InstanceId", instance.getInstanceId());
                        return headers;
                    }
                };
            }
        };
    }


}



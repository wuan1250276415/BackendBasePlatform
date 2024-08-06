package pro.wuan.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * @description:
 * @author: oldone
 * @date: 2021/8/18 16:23
 */
@Slf4j
//@Component
public class LoadBalancerClientFilter implements GlobalFilter, Ordered {
    private final LoadBalancerClient loadBalancer;

    public LoadBalancerClientFilter(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);

        if (url == null || (!"lb".equals(url.getScheme()) && !"lb".equals(schemePrefix))) {
            return chain.filter(exchange);
        }
        //保留原始url
        addOriginalRequestUrl(exchange, url);

        log.trace("LoadBalancerClientFilter url before: " + url);
        //负载均衡到具体服务实例
        final ServiceInstance instance = loadBalancer.choose(url.getHost());
        if (instance == null) {
            throw new NotFoundException("Unable to find instance for " + url.getHost());
        }

        URI uri = exchange.getRequest().getURI();
        //如果没有提供前缀的话，则会使用默认的'< scheme>'，否则使用' lb:< scheme>' 机制。
        String overrideScheme = null;
        if (schemePrefix != null) {
            overrideScheme = url.getScheme();
        }
        //根据获取的服务实例信息，重新组装请求的 url
        URI requestUrl = loadBalancer.reconstructURI(new DelegatingServiceInstance(instance, overrideScheme), uri);
        // Routing 相关 的 GatewayFilter 会 通过 GATEWAY_ REQUEST_ URL_ ATTR 属性， 发起 请求。
        log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
        return chain.filter(exchange);
    }
}

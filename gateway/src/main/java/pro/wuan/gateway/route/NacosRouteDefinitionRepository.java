package pro.wuan.gateway.route;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Slf4j
@Configuration
public class NacosRouteDefinitionRepository implements RouteDefinitionRepository {
    private static final String SCG_DATA_ID = "gateway-router";
    private static final String SCG_GROUP_ID = "cloud";


    private final NacosConfigProperties nacosConfigProperties;

    public NacosRouteDefinitionRepository( NacosConfigProperties nacosConfigProperties) {
        this.nacosConfigProperties = nacosConfigProperties;
    }


    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }

    private List<RouteDefinition> getListByStr(String content) {
        if (CharSequenceUtil.isNotEmpty(content)) {
            return JSON.parseArray(content, RouteDefinition.class);
        }
        return new ArrayList<>(0);
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        try {
            NacosConfigManager nacosConfigManager = new NacosConfigManager(nacosConfigProperties);
            String content = nacosConfigManager.getConfigService().getConfig(SCG_DATA_ID, SCG_GROUP_ID,5000);
            log.info("动态获取网关配置:{}", content);
            List<RouteDefinition> routeDefinitions = getListByStr(content);
            return Flux.fromIterable(routeDefinitions);
        } catch (NacosException e) {
            log.error("getRouteDefinitions by nacos error:{}", e.getMessage());
            return null;
        }
    }
}

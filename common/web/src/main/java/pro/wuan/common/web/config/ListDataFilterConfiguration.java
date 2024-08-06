package pro.wuan.common.web.config;

import org.springframework.context.annotation.Bean;
import pro.wuan.common.db.service.DataFilterService;
import pro.wuan.common.web.service.impl.DataFilterServiceImpl;
import pro.wuan.feignapi.userapi.feign.OrgDataSchemeFeignClient;

/**
 * 列表显示列数据权限配置
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2022-01-05 13:38
 **/
public class ListDataFilterConfiguration {

    @Bean
    public DataFilterService dataFilterService(OrgDataSchemeFeignClient orgDataSchemeFeignClient) {
        return new DataFilterServiceImpl(orgDataSchemeFeignClient);
    }
}

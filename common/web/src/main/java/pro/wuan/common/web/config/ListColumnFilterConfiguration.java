package pro.wuan.common.web.config;

import org.springframework.context.annotation.Bean;
import pro.wuan.common.web.service.IColumnFilterService;
import pro.wuan.common.web.service.impl.ColumnFilterServiceImpl;
import pro.wuan.feignapi.userapi.feign.OrgListColumnFeignClient;

/**
 * 列表显示列数据权限配置
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2022-01-05 13:38
 **/
public class ListColumnFilterConfiguration {

    @Bean
    public IColumnFilterService columnFilterService(OrgListColumnFeignClient orgListColumnFeignClient) {
        return new ColumnFilterServiceImpl(orgListColumnFeignClient);
    }
}

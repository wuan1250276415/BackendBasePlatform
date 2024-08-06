package pro.wuan.common.web.service.impl;

import lombok.extern.slf4j.Slf4j;
import pro.wuan.common.db.service.DataFilterService;
import pro.wuan.feignapi.userapi.feign.OrgDataSchemeFeignClient;

/**
 * 数据权限服务
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2022-01-05 15:31
 **/
@Slf4j
public class DataFilterServiceImpl implements DataFilterService {

    private OrgDataSchemeFeignClient orgDataSchemeFeignClient;

    public DataFilterServiceImpl(OrgDataSchemeFeignClient orgDataSchemeFeignClient) {
        this.orgDataSchemeFeignClient = orgDataSchemeFeignClient;
    }

    /**
     * 获取sql查询条件, feign接口调用
     *
     * @param url
     * @return
     */
    @Override
    public String getSql(String url) {
        String sql = orgDataSchemeFeignClient.findDataSchemeByUrl(url);
        log.info("Feign借口哦返回的数据权限sql是{}", sql);
        return sql;
    }
}

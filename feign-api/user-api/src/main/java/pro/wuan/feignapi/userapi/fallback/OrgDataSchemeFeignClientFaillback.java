package pro.wuan.feignapi.userapi.fallback;

import org.springframework.stereotype.Component;
import pro.wuan.feignapi.userapi.feign.OrgDataSchemeFeignClient;

/**
 * OrgDataSchemeFeignClient
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-12-30 19:41
 **/
@Component
public class OrgDataSchemeFeignClientFaillback implements OrgDataSchemeFeignClient {
    @Override
    public String findDataSchemeByUrl(String url) {
        return null;
    }
}

package pro.wuan.feignapi.userapi.fallback;

import org.springframework.stereotype.Component;
import pro.wuan.feignapi.userapi.entity.OrgListColumnModel;
import pro.wuan.feignapi.userapi.feign.OrgListColumnFeignClient;

import java.util.Collections;
import java.util.List;

/**
 * OrgDataSchemeFeignClient
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-12-30 19:41
 **/
@Component
public class OrgListColumnFeignClientFaillback implements OrgListColumnFeignClient {
    @Override
    public List<OrgListColumnModel> findListColumnByUrl(String url) {
        return Collections.EMPTY_LIST;
    }
}

package pro.wuan.feignapi.appapi.fallback;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.feignapi.appapi.OrgAppModel;
import pro.wuan.feignapi.appapi.feign.AppFeignClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: liumin
 * @date: 2021/9/29 10:47
 */
@Component
public class AppFeignClientFallback implements AppFeignClient {

    /**
     * 根据应用id获取应用
     *
     * @param id 应用id
     * @return 应用
     */
    public OrgAppModel getAppById(@RequestParam("id") Long id) {
        return null;
    }

    /**
     * 根据应用编码获取应用
     *
     * @param code 应用编码
     * @return 应用
     */
    public OrgAppModel getAppByCode(@RequestParam("code") String code) {
        return null;
    }

    /**
     * 根据应用id列表获取应用列表
     *
     * @param idList 应用id列表
     * @return 应用列表
     */
    public List<OrgAppModel> getAppListByIdList(@RequestBody List<Long> idList) {
        return new ArrayList<>();
    }


    public OrgAppModel selectById(Long id) {
        return null;
    }


    public List<OrgAppModel> selectBatchByIds(List<Long> ids) {
        return null;
    }
}

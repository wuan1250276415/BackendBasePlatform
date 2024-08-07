package pro.wuan.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.web.feign.service.impl.QueryFeignService;
import pro.wuan.feignapi.appapi.OrgAppModel;
import pro.wuan.mapper.OrgAppMapper;
import pro.wuan.service.IOrgAppService;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用
 *
 * @author: liumin
 * @date: 2021-08-30 11:22:48
 */
@Slf4j
@RestController
@RequestMapping("/feign/org/app")
public class OrgAppFeignService extends QueryFeignService<IOrgAppService, OrgAppMapper, OrgAppModel> {

    @Autowired
    private IOrgAppService appService;

    /**
     * 根据应用id获取应用
     *
     * @param id 应用id
     * @return 应用
     */
    @GetMapping("/getAppById")
    public OrgAppModel getAppById(@RequestParam("id") Long id) {
        try {
            return appService.getAppById(id);
        } catch (Exception e) {
            log.error("app getAppById error:" + e);
            return null;
        }
    }

    /**
     * 根据应用编码获取应用
     *
     * @param code 应用编码
     * @return 应用
     */
    @GetMapping("/getAppByCode")
    public OrgAppModel getAppByCode(@RequestParam("code") String code) {
        try {
            return appService.getAppByCode(code);
        } catch (Exception e) {
            log.error("app getAppByCode error:" + e);
            return null;
        }
    }

    /**
     * 根据应用id列表获取应用列表
     *
     * @param idList 应用id列表
     * @return 应用列表
     */
    @PostMapping("/getAppListByIdList")
    public List<OrgAppModel> getAppListByIdList(@RequestBody List<Long> idList) {
        try {
            return appService.getAppListByIdList(idList);
        } catch (Exception e) {
            log.error("app getAppListByIdList error:" + e);
            return new ArrayList<>();
        }
    }

}

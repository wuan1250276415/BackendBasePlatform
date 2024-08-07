package pro.wuan.privilege.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.db.service.DataFilterService;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.feign.service.impl.QueryFeignService;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeModel;
import pro.wuan.privilege.mapper.OrgDataSchemeMapper;
import pro.wuan.privilege.service.IOrgDataSchemeService;

/**
 * 数据方案
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@RestController
@RequestMapping("/feign/org/dataScheme")
public class OrgDataSchemeFeignService extends QueryFeignService<IOrgDataSchemeService, OrgDataSchemeMapper, OrgDataSchemeModel> {

    @Autowired
    private DataFilterService dataFilterService;

    /**
     * 根据URL查询当前用户的数据权限
     * @param url
     * @return
     */
    @AspectLog(description = "根据URL查询当前用户的数据权限", type = OperationType.OPERATION_QUERY)
    @GetMapping("/selectByUrl")
    @ResponseBody
    public String findDataSchemeByUrl(@RequestParam("url") String url) {
        return dataFilterService.getSql(url);
    }
}

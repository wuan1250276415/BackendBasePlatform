package pro.wuan.privilege.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.feign.service.impl.QueryFeignService;
import pro.wuan.feignapi.userapi.entity.OrgListColumnModel;
import pro.wuan.privilege.mapper.OrgListColumnMapper;
import pro.wuan.privilege.service.IOrgListColumnService;

import java.util.List;

/**
 * 列表字段
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@RestController
@RequestMapping("/feign/org/listColumn")
public class OrgListColumnFeignService extends QueryFeignService<IOrgListColumnService, OrgListColumnMapper, OrgListColumnModel> {

    @Autowired
    private IOrgListColumnService orgListColumnService;

    /**
     * 根据URL查询当前用户的列表显示权限
     * @param url
     * @return
     */
    @AspectLog(description = "根据URL查询当前用户的数据权限", type = OperationType.OPERATION_QUERY)
    @GetMapping("/selectByUrl")
    @ResponseBody
    public List<OrgListColumnModel> findListColumnByUrl(@RequestParam("url") String url) {
        return orgListColumnService.getColumnListFilterByCurrentUser(url);
    }
}

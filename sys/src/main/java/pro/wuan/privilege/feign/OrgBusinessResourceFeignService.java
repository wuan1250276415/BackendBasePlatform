package pro.wuan.privilege.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.web.feign.service.impl.QueryFeignService;
import pro.wuan.feignapi.userapi.entity.OrgBusinessResourceModel;
import pro.wuan.feignapi.userapi.vo.SearchMenuVO;
import pro.wuan.privilege.mapper.OrgBusinessResourceMapper;
import pro.wuan.privilege.service.IOrgBusinessResourceService;

import java.util.List;

/**
 * 业务资源
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Slf4j
@RestController
@RequestMapping("/feign/org/businessResource")
public class OrgBusinessResourceFeignService extends QueryFeignService<IOrgBusinessResourceService, OrgBusinessResourceMapper, OrgBusinessResourceModel> {

    @Autowired
    private IOrgBusinessResourceService businessResourceService;

    /**
     * 获取所有搜索菜单列表
     *
     * @return 搜索菜单列表
     */
    @GetMapping("/getAllSearchMenuList")
    public List<SearchMenuVO> getAllSearchMenuList() {
        try {
            return businessResourceService.getAllSearchMenuList();
        } catch (Exception e) {
            log.error("businessResource getAllSearchMenuList error:" + e);
            return null;
        }
    }

}

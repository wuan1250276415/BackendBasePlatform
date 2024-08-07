package pro.wuan.element.feign;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.web.feign.service.impl.QueryFeignService;
import pro.wuan.element.mapper.OrgElementMapper;
import pro.wuan.element.service.IOrgElementService;
import pro.wuan.feignapi.userapi.entity.OrgElementModel;

/**
 * 元素
 *
 * @author: liumin
 * @date: 2021-08-30 09:49:57
 */
@RestController
@RequestMapping("/feign/org/element")
public class OrgElementFeignService extends QueryFeignService<IOrgElementService, OrgElementMapper, OrgElementModel> {

}

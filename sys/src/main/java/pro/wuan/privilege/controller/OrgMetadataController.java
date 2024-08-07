package pro.wuan.privilege.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.OrgMetadataModel;
import pro.wuan.privilege.mapper.OrgMetadataMapper;
import pro.wuan.privilege.service.IOrgMetadataService;

/**
 * 元数据
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@RestController
@RequestMapping("/org/metadata")
public class OrgMetadataController extends CURDController<IOrgMetadataService, OrgMetadataMapper, OrgMetadataModel> {

}

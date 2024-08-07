package pro.wuan.privilege.service.impl;

import org.springframework.stereotype.Service;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.OrgMetadataModel;
import pro.wuan.privilege.mapper.OrgMetadataMapper;
import pro.wuan.privilege.service.IOrgMetadataService;

/**
 * 元数据
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Service("orgMetadataService")
public class OrgMetadataServiceImpl extends BaseServiceImpl<OrgMetadataMapper, OrgMetadataModel> implements IOrgMetadataService {

}

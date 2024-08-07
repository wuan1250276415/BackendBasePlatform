package pro.wuan.privilege.service.impl;

import org.springframework.stereotype.Service;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.OrgMetadataColumnModel;
import pro.wuan.privilege.mapper.OrgMetadataColumnMapper;
import pro.wuan.privilege.service.IOrgMetadataColumnService;

/**
 * 元数据字段
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Service("orgMetadataColumnService")
public class OrgMetadataColumnServiceImpl extends BaseServiceImpl<OrgMetadataColumnMapper, OrgMetadataColumnModel> implements IOrgMetadataColumnService {

}

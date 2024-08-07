package pro.wuan.privilege.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgMetadataModel;

/**
 * 元数据
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Mapper
public interface OrgMetadataMapper extends IBaseMapper<OrgMetadataModel> {
	
}

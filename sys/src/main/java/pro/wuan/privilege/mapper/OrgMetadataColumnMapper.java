package pro.wuan.privilege.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgMetadataColumnModel;

/**
 * 元数据字段
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Mapper
public interface OrgMetadataColumnMapper extends IBaseMapper<OrgMetadataColumnModel> {
	
}

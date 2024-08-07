package pro.wuan.organ.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;

/**
 * 组织机构
 *
 * @author: liumin
 * @date: 2021-08-30 11:21:11
 */
@Mapper
public interface OrgOrganMapper extends IBaseMapper<OrgOrganModel> {
	
}

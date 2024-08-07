package pro.wuan.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.appapi.OrgAppModel;

/**
 * 应用
 *
 * @author: liumin
 * @date: 2021-08-30 11:22:48
 */
@Mapper
public interface OrgAppMapper extends IBaseMapper<OrgAppModel> {
	
}

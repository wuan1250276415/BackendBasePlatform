package pro.wuan.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgUserPostModel;

/**
 * 用户岗位关联表
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
@Mapper
public interface OrgUserPostMapper extends IBaseMapper<OrgUserPostModel> {
	
}

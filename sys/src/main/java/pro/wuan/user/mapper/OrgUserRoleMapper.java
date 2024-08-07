package pro.wuan.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgUserRoleModel;

/**
 * 用户角色关联表
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
@Mapper
public interface OrgUserRoleMapper extends IBaseMapper<OrgUserRoleModel> {
	
}

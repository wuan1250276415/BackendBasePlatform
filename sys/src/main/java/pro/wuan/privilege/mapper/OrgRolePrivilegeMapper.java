package pro.wuan.privilege.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgRolePrivilegeModel;

/**
 * 角色权限关联表
 *
 * @author: liumin
 * @create 2021-08-30 11:15:00
 */
@Mapper
public interface OrgRolePrivilegeMapper extends IBaseMapper<OrgRolePrivilegeModel> {
	
}

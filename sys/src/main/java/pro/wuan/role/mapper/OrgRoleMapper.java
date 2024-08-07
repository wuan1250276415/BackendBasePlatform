package pro.wuan.role.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgRoleModel;

import java.util.List;

/**
 * 角色
 *
 * @author: liumin
 * @date: 2021-08-30 11:15:00
 */
@Mapper
public interface OrgRoleMapper extends IBaseMapper<OrgRoleModel> {

    /**
     * 根据用户id获取关联的角色列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 角色列表
     */
    public List<OrgRoleModel> getRoleListByUserId(@Param("userId") Long userId, @Param("appId") Long appId);
	
}

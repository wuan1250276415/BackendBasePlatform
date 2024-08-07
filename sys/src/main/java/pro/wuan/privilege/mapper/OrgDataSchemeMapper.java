package pro.wuan.privilege.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeModel;

import java.util.List;

/**
 * 数据方案
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Mapper
public interface OrgDataSchemeMapper extends IBaseMapper<OrgDataSchemeModel> {

    /**
     * 根据用户id，应用id获取关联的数据方案列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 数据方案列表
     */
    public List<OrgDataSchemeModel> getDataSchemeListByUserAndApp(@Param("userId") Long userId, @Param("appId") Long appId);

    /**
     * 根据角色id列表获取关联的数据方案列表
     *
     * @param roleIdList 角色id列表
     * @return 数据方案列表
     */
    public List<OrgDataSchemeModel> getDataSchemeListByRoleIdList(@Param("roleIdList") List<Long> roleIdList);
	
}

package pro.wuan.privilege.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgBusinessResourceModel;
import pro.wuan.feignapi.userapi.vo.SearchMenuVO;

import java.util.List;

/**
 * 业务资源
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Mapper
public interface OrgBusinessResourceMapper extends IBaseMapper<OrgBusinessResourceModel> {

    /**
     * 根据用户id，应用id获取关联的菜单列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 菜单列表
     */
    public List<OrgBusinessResourceModel> getMenuListByUserAndApp(@Param("userId") Long userId, @Param("appId") Long appId);

    /**
     * 根据用户id，应用id获取关联的功能列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 功能列表
     */
    public List<OrgBusinessResourceModel> getFunctionListByUserAndApp(@Param("userId") Long userId, @Param("appId") Long appId);

    /**
     * 根据角色id列表获取关联的业务资源列表
     *
     * @param roleIdList 角色id列表
     * @return 业务资源列表
     */
    public List<OrgBusinessResourceModel> getBusinessResourceListByRoleIdList(@Param("roleIdList") List<Long> roleIdList);

    /**
     * 获取所有搜索菜单列表
     *
     * @return 搜索菜单列表
     */
    public List<SearchMenuVO> getAllSearchMenuList();

}

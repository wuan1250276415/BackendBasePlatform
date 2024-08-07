package pro.wuan.privilege.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgListColumnModel;

import java.util.List;

/**
 * 列表字段
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Mapper
public interface OrgListColumnMapper extends IBaseMapper<OrgListColumnModel> {

    /**
     * 根据应用id获取关联的列表字段列表
     *
     * @param appId 应用id
     * @return 列表字段列表
     */
    public List<OrgListColumnModel> getListColumnListByAppId(@Param("appId") Long appId);

    /**
     * 根据用户id，应用id获取关联的列表字段列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 列表字段列表
     */
    public List<OrgListColumnModel> getListColumnListByUserAndApp(@Param("userId") Long userId, @Param("appId") Long appId);

    /**
     * 根据角色id列表获取关联的列表字段列表
     *
     * @param roleIdList 角色id列表
     * @return 列表字段列表
     */
    public List<OrgListColumnModel> getListColumnListByRoleIdList(@Param("roleIdList") List<Long> roleIdList);

}

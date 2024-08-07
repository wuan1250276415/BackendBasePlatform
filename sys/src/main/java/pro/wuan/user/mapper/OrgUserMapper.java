package pro.wuan.user.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;

import java.util.List;

/**
 * 用户
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
@Mapper
public interface OrgUserMapper extends IBaseMapper<OrgUserModel> {

    /**
     * 分页查询角色关联的用户列表
     *
     * @param mppage
     * @param roleId
     * @param userName
     * @return
     */
    IPage<OrgUserModel> selectPageByRole(Page<OrgUserModel> mppage, @Param("param") PageSearchParam pageSearchParam,
                                         @Param("roleId") Long roleId, @Param("userName") String userName);

    /**
     * 根据组织机构id列表查询关联的用户列表
     *
     * @param organIdList 组织机构id
     * @return 用户列表
     */
    List<OrgUserModel> getUserListByOrganIdList(@Param("organIdList") List<Long> organIdList);

    /**
     * 根据组织机构id列表查询关联的用户列表- 剔除超管和单位管理员
     *
     *
     * @param organIdList 组织机构id
     * @return 用户列表
     */
    List<OrgUserModel> getUserListByOrganIdListRemoveAdmin(@Param("organIdList") List<Long> organIdList);

}

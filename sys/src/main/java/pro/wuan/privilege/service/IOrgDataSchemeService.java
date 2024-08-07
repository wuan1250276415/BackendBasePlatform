package pro.wuan.privilege.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.constraints.NotEmpty;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeColumnModel;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeModel;
import pro.wuan.privilege.mapper.OrgDataSchemeMapper;
import pro.wuan.privilege.vo.ConditionGroup;
import pro.wuan.privilege.vo.OrgDataSchemeVO;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 数据方案
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
public interface IOrgDataSchemeService extends IBaseService<OrgDataSchemeMapper, OrgDataSchemeModel> {

    /**
     * 分页查询数据方案字段
     *
     * @param pageSearchParam 分页查询内容
     * @return 数据方案字段实体分页列表
     */
    public Result<IPage<OrgDataSchemeColumnModel>> selectDataSchemeColumnPage(PageSearchParam pageSearchParam);

    /**
     * 保存数据方案
     *
     * @param schemeVO 数据方案VO
     * @return 数据方案实体
     */
    public Result<OrgDataSchemeModel> saveDataScheme(OrgDataSchemeVO schemeVO);

    /**
     * 更新数据方案
     *
     * @param schemeVO 数据方案VO
     * @return 数据方案实体
     */
    public Result<OrgDataSchemeModel> updateDataScheme(OrgDataSchemeVO schemeVO);

    /**
     * 解析条件分组列表
     *
     * @param conditionGroupList
     * @return
     */
    public String parseConditionGroupList(List<ConditionGroup> conditionGroupList);

    /**
     * 删除数据方案
     *
     * @param id 数据方案id
     * @return 删除成功/失败
     */
    public Result<String> deleteDataScheme(@NotNull(message = "id不能为空") Long id);

    /**
     * 根据用户id，应用id获取关联的数据方案列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 数据方案列表
     */
    public List<OrgDataSchemeModel> getDataSchemeListByUserAndApp(@NotNull(message = "userId不能为空") Long userId, @NotNull(message = "appId不能为空") Long appId);

    /**
     * 根据角色id列表获取关联的数据方案列表
     *
     * @param roleIdList 角色id列表
     * @return 数据方案列表
     */
    public List<OrgDataSchemeModel> getDataSchemeListByRoleIdList(@NotEmpty(message = "roleIdList不能为空") List<Long> roleIdList);

    /**
     * 批量删除菜单资源id关联的数据方案
     *
     * @param menuResourceId
     */
    public int batchDeleteDataScheme(@NotNull(message = "menuResourceId不能为空") Long menuResourceId);

}
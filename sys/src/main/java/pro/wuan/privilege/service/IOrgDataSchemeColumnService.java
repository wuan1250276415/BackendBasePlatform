package pro.wuan.privilege.service;


import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeColumnModel;
import pro.wuan.privilege.mapper.OrgDataSchemeColumnMapper;
import pro.wuan.privilege.vo.DataSchemeColumnMap;

import javax.validation.constraints.NotNull;

/**
 * 数据方案字段
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
public interface IOrgDataSchemeColumnService extends IBaseService<OrgDataSchemeColumnMapper, OrgDataSchemeColumnModel> {

    /**
     * 保存数据方案字段
     *
     * @param columnModel 数据方案字段实体
     * @return 数据方案字段实体
     */
    public Result<OrgDataSchemeColumnModel> saveDataSchemeColumn(OrgDataSchemeColumnModel columnModel);

    /**
     * 更新数据方案字段
     *
     * @param columnModel 数据方案字段实体
     * @return 数据方案字段实体
     */
    public Result<OrgDataSchemeColumnModel> updateDataSchemeColumn(OrgDataSchemeColumnModel columnModel);

    /**
     * 删除数据方案字段
     *
     * @param id 数据方案字段id
     * @return 删除成功/失败
     */
    public Result<String> deleteDataSchemeColumn(@NotNull(message = "id不能为空") Long id);

    /**
     * 根据菜单资源id获取数据方案字段信息集合
     *
     * @param menuResourceId 菜单资源id
     * @return 数据方案字段信息集合
     */
    public DataSchemeColumnMap getDataSchemeColumnMap(@NotNull(message = "menuResourceId不能为空") Long menuResourceId);

    /**
     * 批量删除菜单资源id关联的数据方案字段及条件符号关联表
     *
     * @param menuResourceId
     */
    public void batchDeleteSchemeColumnAndSymbol(@NotNull(message = "menuResourceId不能为空") Long menuResourceId);

}
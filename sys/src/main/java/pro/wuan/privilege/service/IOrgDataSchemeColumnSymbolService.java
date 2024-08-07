package pro.wuan.privilege.service;

import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeColumnSymbolModel;
import pro.wuan.privilege.mapper.OrgDataSchemeColumnSymbolMapper;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 数据方案字段、条件符号关联表
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
public interface IOrgDataSchemeColumnSymbolService extends IBaseService<OrgDataSchemeColumnSymbolMapper, OrgDataSchemeColumnSymbolModel> {

    /**
     * 批量删除数据方案字段id关联的数据方案字段、条件符号关联表
     *
     * @param dataSchemeColumnId
     */
    public int batchDeleteDataSchemeColumnSymbol(@NotNull(message = "dataSchemeColumnId不能为空") Long dataSchemeColumnId);

    /**
     * 根据数据方案字段id获取关联的条件符号关联表列表
     *
     * @param dataSchemeColumnId
     * @return
     */
    public List<OrgDataSchemeColumnSymbolModel> getDataSchemeColumnSymbolByColumn(@NotNull(message = "dataSchemeColumnId不能为空") Long dataSchemeColumnId);

}
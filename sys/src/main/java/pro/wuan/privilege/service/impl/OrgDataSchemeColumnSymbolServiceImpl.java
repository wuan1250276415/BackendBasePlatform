package pro.wuan.privilege.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.wuan.common.db.convertor.db2page.DbConvert2Page;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeColumnSymbolModel;
import pro.wuan.privilege.mapper.OrgDataSchemeColumnSymbolMapper;
import pro.wuan.privilege.service.IOrgDataSchemeColumnSymbolService;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 数据方案字段、条件符号关联表
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Service("orgDataSchemeColumnSymbolService")
public class OrgDataSchemeColumnSymbolServiceImpl extends BaseServiceImpl<OrgDataSchemeColumnSymbolMapper, OrgDataSchemeColumnSymbolModel> implements IOrgDataSchemeColumnSymbolService {

    /**
     * 批量删除数据方案字段id关联的数据方案字段、条件符号关联表
     *
     * @param dataSchemeColumnId
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteDataSchemeColumnSymbol(@NotNull(message = "dataSchemeColumnId不能为空") Long dataSchemeColumnId) {
        LambdaQueryWrapper<OrgDataSchemeColumnSymbolModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgDataSchemeColumnSymbolModel::getDataSchemeColumnId, dataSchemeColumnId);
        return this.delete(wrapper);
    }

    /**
     * 根据数据方案字段id获取关联的条件符号关联表列表
     *
     * @param dataSchemeColumnId
     * @return
     */
    public List<OrgDataSchemeColumnSymbolModel> getDataSchemeColumnSymbolByColumn(@NotNull(message = "dataSchemeColumnId不能为空") Long dataSchemeColumnId) {
        LambdaQueryWrapper<OrgDataSchemeColumnSymbolModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgDataSchemeColumnSymbolModel::getDataSchemeColumnId, dataSchemeColumnId);
        List<OrgDataSchemeColumnSymbolModel> symbolModelList = this.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(symbolModelList)) {
            DbConvert2Page dbConvert2Page = new DbConvert2Page();
            dbConvert2Page.executeDbToPage4List(symbolModelList);
        }
        return symbolModelList;
    }

}

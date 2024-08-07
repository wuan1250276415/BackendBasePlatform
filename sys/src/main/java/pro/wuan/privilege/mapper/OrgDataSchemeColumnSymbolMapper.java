package pro.wuan.privilege.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeColumnSymbolModel;

/**
 * 数据方案字段、条件符号关联表
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Mapper
public interface OrgDataSchemeColumnSymbolMapper extends IBaseMapper<OrgDataSchemeColumnSymbolModel> {
	
}

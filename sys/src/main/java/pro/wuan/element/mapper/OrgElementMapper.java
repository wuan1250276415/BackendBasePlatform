package pro.wuan.element.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgElementModel;

/**
 * 元素
 *
 * @author: liumin
 * @date: 2021-08-30 09:49:57
 */
@Mapper
public interface OrgElementMapper extends IBaseMapper<OrgElementModel> {
	
}

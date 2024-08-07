package pro.wuan.layout.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.Layout;

@Mapper
public interface LayoutMapper extends IBaseMapper<Layout> {
}

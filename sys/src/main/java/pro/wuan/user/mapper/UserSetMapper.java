package pro.wuan.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.UserSet;

@Mapper
public interface UserSetMapper extends IBaseMapper<UserSet> {
}

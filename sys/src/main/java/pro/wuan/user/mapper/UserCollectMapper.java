package pro.wuan.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.UserCollect;

@Mapper
public interface UserCollectMapper extends IBaseMapper<UserCollect> {
}

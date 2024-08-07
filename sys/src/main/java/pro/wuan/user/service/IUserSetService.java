package pro.wuan.user.service;

import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.UserSet;
import pro.wuan.user.mapper.UserSetMapper;

public interface IUserSetService extends IBaseService<UserSetMapper, UserSet> {


    Result save(UserSet userSet);

    UserSet getByUserId(Long userId);

}

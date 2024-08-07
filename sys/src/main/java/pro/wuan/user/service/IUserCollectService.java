package pro.wuan.user.service;


import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.UserCollect;
import pro.wuan.user.mapper.UserCollectMapper;

import java.util.List;

public interface IUserCollectService extends IBaseService<UserCollectMapper, UserCollect> {

    /**
     * 批量修改
     * @param collects
     * @return
     */
    Result batchUpdate(List<UserCollect> collects);
}

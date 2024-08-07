package pro.wuan.skin.service;


import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.SysSkin;
import pro.wuan.skin.mapper.SysSkinMapper;

public interface ISysSkinService extends IBaseService<SysSkinMapper, SysSkin> {

    //添加系统皮肤
    Result save(SysSkin sysSkin);


}

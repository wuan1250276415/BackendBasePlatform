package pro.wuan.layout.service;


import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.Layout;
import pro.wuan.layout.mapper.LayoutMapper;

public interface ILayoutService extends IBaseService<LayoutMapper, Layout> {

    //添加系统布局
    Result save(Layout layout);


}

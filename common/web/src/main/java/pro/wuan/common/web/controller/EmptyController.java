package pro.wuan.common.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.service.IBaseService;

import java.lang.reflect.ParameterizedType;

/**
 * @program: tellhowcloud
 * @description: 非CURDController
 * @author: HawkWang
 * @create: 2021-09-06 10:29
 **/
public abstract class EmptyController<Service extends IBaseService, Mapper extends IBaseMapper, IdModel extends IDModel> implements IBaseController<Mapper, IdModel> {

    Class<IdModel> modelClass = null;

    @Autowired
    protected Service service;

    /**
     * 获取实体类
     *
     * @return
     */
    @Override
    public Class getModelClass() {
        if (modelClass == null) {
            this.modelClass = (Class<IdModel>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];
        }
        return this.modelClass;
    }

    /**
     * 获取实体类对应的Service
     *
     * @return
     */
    @Override
    public IBaseService getBaseService() {
        return service;
    }
}

package pro.wuan.common.web.feign.fallback.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.web.feign.fallback.IBaseFeignClientFallback;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: tellhowcloud
 * @description: 查询服务降级
 * @author: HawkWang
 * @create: 2021-08-27 14:30
 **/
public abstract class QueryFeignClientFallback<IdModel extends IDModel> implements IBaseFeignClientFallback {

    Class<IdModel> modelClass = null;


    @Override
    public Class<IdModel> getModelClass() {
        if (modelClass == null) {
            this.modelClass = (Class<IdModel>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return this.modelClass;
    }

    @Override
    public IdModel selectById(Long id) {
        try {
            return getModelClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<IdModel> selectByProperty(IDModel idModel) {
        return new ArrayList();
    }

    @Override
    public IPage<IdModel> selectPage(PageSearchParam pageSearchParam) {
        return new Page();
    }
}

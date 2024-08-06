package pro.wuan.common.web.feign.service;


import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.db.convertor.db2page.DbConvert2Page;

import java.util.List;

/**
 * @program: tellhowcloud
 * @description: 微服务实现基类
 * @author: HawkWang
 * @create: 2021-08-27 11:38
 **/
public interface IBaseFeignService<Mapper extends IBaseMapper, IdModel extends IDModel> {

    /**
     * 获取实体类
     *
     * @return
     */
    Class<IdModel> getModelClass();

    /**
     * 获取实体类对应的Service
     *
     * @return
     */
    IBaseService<Mapper, IdModel> getBaseService();

    /**
     * 默认转换实体对象的注解
     *
     * @param idModel
     */
    default void db2page(IdModel idModel) {
        if (null == idModel) {
            return;
        }
        DbConvert2Page dbConvert2Page = new DbConvert2Page();
        dbConvert2Page.executeDbToPage4Model(idModel);
    }

    /**
     * 直接转换实体列表的注解
     *
     * @param idModels
     */
    default void db2pageList(List<IdModel> idModels) {
        if (null == idModels) {
            return;
        }
        DbConvert2Page dbConvert2Page = new DbConvert2Page();
        dbConvert2Page.executeDbToPage4List(idModels);
    }
}

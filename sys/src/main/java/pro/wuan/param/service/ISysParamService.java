package pro.wuan.param.service;


import model.SysParamModel;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.param.mapper.SysParamMapper;

import java.util.Map;

/**
 * 系统参数
 *
 * @author HawkWang
 * @program: tellhowcloud
 * @create 2021-09-06 17:20:20
 */
public interface ISysParamService extends IBaseService<SysParamMapper, SysParamModel> {

    /**
     * 更新指定系统的系统参数
     * @param appCode
     */
    void saveSysParamInRedis(String appCode);

    /**
     * 后台系统内部，根据Class返回对应的系统参数对象。
     * @param t
     * @param <T>
     * @return
     */
    <T> T selectSysParamByParamClass(Class<T> t);

    /**
     * feign服务接口调用获取外部应用的系统参数
     * @param className
     * @return
     */
    Map<String, Object> selectSysParamByParamClass(String className);

    /**
     * 重新加载系统中所有的参数到redis中
     */
    void reloadSysParam();
}
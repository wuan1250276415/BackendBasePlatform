package pro.wuan.param.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import model.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.utils.ConvertUtil;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.common.redis.util.JedisUtil;
import pro.wuan.param.mapper.SysParamMapper;
import pro.wuan.param.service.ISysParamService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统参数
 *
 * @author HawkWang
 * @program: tellhowcloud
 * @create 2021-09-06 17:20:20
 */
@Service("sysParamService")
public class SysParamServiceImpl extends BaseServiceImpl<SysParamMapper, SysParamModel> implements ISysParamService {

    @Autowired
    private JedisUtil jedisUtil;

    /**
     * 根据appCode把对应的sysparamModel更新到redis中
     *
     * @param appCode
     */
    @Override
    public void saveSysParamInRedis(String appCode) {
        LambdaQueryWrapper<SysParamModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysParamModel::getAppCode, appCode);
        List<SysParamModel> sysParamModels = dao.selectList(lambdaQueryWrapper);
        Map<String, List<SysParamModel>> sysParamModelMap = sysParamModels.stream().collect(Collectors.groupingBy(SysParamModel::getParamClass));
        for (String key : sysParamModelMap.keySet()) {
            List<SysParamModel> finalSysParamModel = sysParamModelMap.get(key);
            Map<String, Object> map = finalSysParamModel.stream().collect(Collectors.toMap(SysParamModel::getParamCode, SysParamModel::getParamValue));
            jedisUtil.set(key, map);
        }
    }

    /**
     * 后台系统内部，根据Class返回对应的系统参数对象。
     *
     * @param t
     * @return
     */
    @Override
    public <T> T selectSysParamByParamClass(Class<T> t) {
        String className = t.getName();
        Map<String, Object> map = selectSysParamByParamClass(className);
        T param = ConvertUtil.mapToObject(map, t);
        return param;
    }

    /**
     * feign服务接口调用获取外部应用的系统参数
     *
     * @param className
     * @return
     */
    @Override
    public Map<String, Object> selectSysParamByParamClass(String className) {
        Object obj = jedisUtil.get(className);
        if (null == obj) {
            LambdaQueryWrapper<SysParamModel> finalWrapper = new LambdaQueryWrapper<>();
            finalWrapper.eq(SysParamModel::getParamClass, className);
            List<SysParamModel> finalSysParamModel = dao.selectList(finalWrapper);
            if (finalSysParamModel.size() == 0) {
                throw new RuntimeException("找不到当前的系统参数对象[" + className + "]");
            }
            obj = finalSysParamModel.stream().collect(Collectors.toMap(SysParamModel::getParamCode, SysParamModel::getParamValue));
            jedisUtil.set(className, obj);
        }
        return (Map<String, Object>) obj;
    }

    /**
     * 重新加载系统中所有的参数到redis中
     */
    @Override
    public void reloadSysParam() {
        LambdaQueryWrapper<SysParamModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.groupBy(SysParamModel::getAppCode).select(SysParamModel::getAppCode);
        List<SysParamModel> sysParamModels = dao.selectList(lambdaQueryWrapper);
        sysParamModels.forEach(item -> {
            saveSysParamInRedis(item.getAppCode());
        });
    }
}

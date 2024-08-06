package pro.wuan.common.db.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import pro.wuan.common.core.constant.CommonConstant;

import java.util.Map;

/**
 * 数据权限过滤
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
public interface DataFilterService {

    /**
     * 获取sql查询条件, feign接口调用
     * @param url
     * @return
     */
    String getSql(String url);

    /**
     * 获取sql查询条件
     *
     * @param url 请求访问路径
     * @return
     */
    default String getSql(String url, Map<String,Object> systemParams){
       return this.getSql(url);
    }



    /**
     * 设置默认的数据权限过滤，默认的是当前用户自己创建的
     * @param queryWrapper
     * @param systemParams
     */
    default void setDefaultDataFilterCondition(QueryWrapper queryWrapper, Map<String, Object> systemParams){
        // 获取当前登录用户id
        Long userId = (Long) systemParams.get(CommonConstant.DATA_FILTER_SYSTEM_PARAMS_USER_ID);
        queryWrapper.eq("create_user_id", userId);
    };


    /**
     * 扩展额外的数据权限替换，需要override当前方法。
     * @param conditionJson
     * @return 解析后的数据库sql
     */
    default String customParseParameter(String conditionJson){
        return conditionJson;
    }

}

package pro.wuan.common.core.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.*;

import java.util.List;
import java.util.Map;

public interface IBaseService<Dao extends IBaseMapper, T extends IDModel> {

    // 新增数据
    int insert(T t);

    // 批量新增
    int batchInsertNoCascade(List<T> entityList);

    // 根据主键删除
    int deleteById(Long id);

    int deleteByMap(Map<String, Object> parameters);

    int delete(Wrapper wrapper);

    int deleteBatchIds(List<Long> ids);

    int updateById(T t);

    int update(T t, Wrapper wrapper);

    int updateAllById(T t);

    int updateByIdWithCascade(T t);

    int updateWithCascade(T t, Wrapper wrapper);

    int updateAllByIdWithCascade(T t);

    boolean batchUpdateNoCascade(List<T> entityList);

    T selectById(Long id);

    // 批量查找
    List<T> selectBatchIds(List<Long> ids);

    // 根据指定条件搜索
    List<T> selectByMap(Map<String, Object> parameters);

    T selectOne(Wrapper<T> queryWrapper);

    Long selectCount(Wrapper wrapper);

    List<T> selectList(Wrapper wrapper);

    List<Map<String, Object>> selectMaps(Wrapper wrapper);

    IPage<T> selectPage(IPage page, Wrapper wrapper);

    // 自定义翻页查询功能
    Result<IPage<T>> selectPage(PageSearchParam pageSearchParam);

    // 自定义翻页查询功能（数据权限过滤）
    @Deprecated
    Result<IPage<T>> selectPageByDataFilter(PageSearchParam pageSearchParam, HttpServletRequest request);

    // 新自定义翻页查询功能（数据权限过滤），原接口已过期不再使用
    Result<IPage<T>> selectPageByDataFilter(PageSearchParam pageSearchParam, HttpServletRequest request, Map<String, Object> systemParams);
    /**
     * 扩展数据权限过滤查询条件
     *
     * @param queryWrapper 查询对象
     * @param systemParams 系统参数
     * @return
     */
    void extendDataFilterCondition(QueryWrapper<T> queryWrapper, Map<String, Object> systemParams);

    default Boolean isExists(String param) {
        return true;
    }

    /**
     * ----->租户查询开始
     */

    //在租户的数据范围内查找一个数据
    T selectOneWithTenantId(LambdaQueryWrapper<T> queryWrapper);

    //在租户的数据范围内查找一个数据
    T selectOneWithTenantId(QueryWrapper<T> queryWrapper);

    //在租户的数据范围内统计一个数据
    Long selectCountWithTenantId(QueryWrapper<T> wrapper);

    //在租户的数据范围内查找数据列表
    List<T> selectListWithTenantId(QueryWrapper<T> wrapper);

    //在租户的数据范围内查找不同的属性
    List<Map<String, Object>> selectMapsWithTenantId(QueryWrapper<T> wrapper);

    //在租户的数据范围内查找分页列表
    IPage<T> selectPageWithTenantId(IPage page, QueryWrapper<T> wrapper);

    /**
     * ----->租户查询结束
     */

    //根据参数配置过滤列表
    List<T> selectListByParam(Map<String, BaseQueryValue> queryPrams);


    //根据参数配置过滤列表
    List<T> selectListByBaseParam(BaseSearchParam baseSearchParam);

}

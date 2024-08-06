package pro.wuan.common.db.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.transaction.annotation.Transactional;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.constant.SearchOptConstant;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.*;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.core.utils.GenericsUtils;
import pro.wuan.common.core.utils.SpringBeanUtil;
import pro.wuan.common.db.service.DataFilterService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

// 基础service类
@Slf4j
public class BaseServiceImpl<Dao extends IBaseMapper, T extends IDModel> extends ServiceImpl implements IBaseService<Dao, T> {

    @Resource
    protected Dao dao;

    @Resource
    private DispatcherService dispatcher;

    private DataFilterService dataFilterService;

    private DataFilterService getDataFilterService() {
        if (dataFilterService == null) {
            dataFilterService = SpringBeanUtil.getBeanIgnoreEx(DataFilterService.class);
        }
        return dataFilterService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(T t) {
        int result = dao.insert(t);
        if (result > 0) {
            dispatcher.dispatcherSave(t);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertNoCascade(List<T> entityList) {
        if (entityList.isEmpty()) {
            return 0;
        }
        if (entityList.size() > 50000) {
            throw new RuntimeException("记录数大于50000，不能保存");
        }
        return dao.batchInsertNoCascade(entityList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Long id) {
        return dao.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByMap(Map<String, Object> parameters) {
        return dao.deleteByMap(parameters);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Wrapper wrapper) {
        return dao.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatchIds(List<Long> ids) {
        return dao.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateById(T t) {
        int result = dao.updateById(t);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(T t, Wrapper wrapper) {
        int result = dao.update(t, wrapper);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAllById(T t) {
        int result = dao.updateAllById(t);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByIdWithCascade(T t) {
        int result = dao.updateById(t);
        if (result > 0) {
            dispatcher.dispatcherUpdate(t);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWithCascade(T t, Wrapper wrapper) {
        int result = dao.update(t, wrapper);
        if (result > 0) {
            dispatcher.dispatcherUpdate(t);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAllByIdWithCascade(T t) {
        int result = dao.updateAllById(t);
        if (result > 0) {
            dispatcher.dispatcherUpdate(t);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean batchUpdateNoCascade(List<T> entityList) {
        for (T entity : entityList) {
            dao.updateById(entity);
        }
        return true;
//        return super.updateBatchById(entityList);
    }

    @Override
    public T selectById(Long id) {
        return (T) dao.selectById(id);
    }

    @Override
    public List<T> selectBatchIds(List<Long> ids) {
        return dao.selectBatchIds(ids);
    }

    @Override
    public List<T> selectByMap(Map<String, Object> parameters) {
        return dao.selectByMap(parameters);
    }

    @Override
    public T selectOne(Wrapper<T> wrapper) {
        return (T) dao.selectOne(wrapper);
    }

    @Override
    public Long selectCount(Wrapper wrapper) {
        return dao.selectCount(wrapper);
    }

    @Override
    public List<T> selectList(Wrapper wrapper) {
        return dao.selectList(wrapper);
    }

    @Override
    public List<Map<String, Object>> selectMaps(Wrapper wrapper) {
        return dao.selectMaps(wrapper);
    }

    @Override
    public IPage<T> selectPage(IPage page, Wrapper wrapper) {
        return dao.selectPage(page, wrapper);
    }

    @Override
    public Result<IPage<T>> selectPage(PageSearchParam pageSearchParam) {
        return this.selectPageByParam(pageSearchParam, null, null);
    }

    @Deprecated
    @Override
    public Result<IPage<T>> selectPageByDataFilter(PageSearchParam pageSearchParam, HttpServletRequest request) {
        return this.selectPageByParam(pageSearchParam, request, null);
    }

    @Override
    public Result<IPage<T>> selectPageByDataFilter(PageSearchParam pageSearchParam, HttpServletRequest request, Map<String, Object> systemParams) {
        return this.selectPageByParam(pageSearchParam, request, systemParams);
    }

    /**
     * 分页查询
     *
     * @param pageSearchParam
     * @param request
     * @param systemParams
     * @return
     */
    private Result<IPage<T>> selectPageByParam(PageSearchParam pageSearchParam, HttpServletRequest request, Map<String, Object> systemParams) {
        int limit = pageSearchParam.getLimit();
        int current = pageSearchParam.getPage();
        Page page = new Page<>(current, limit);
        // 开始执行queryWrapper封装
        QueryWrapper<T> queryWrapper = null;
        if (pageSearchParam.getWrapper() != null) {
            queryWrapper = pageSearchParam.getWrapper();
        } else {
            queryWrapper = new QueryWrapper<>();
        }
        // 查询参数封装
        Map<String, ColumnCache> columnCacheMap = LambdaUtils.getColumnMap(GenericsUtils.getSuperClassGenricType(this.getClass(), 1));

        // 解析查询条件
        addQueryForQueryWrapper(queryWrapper, columnCacheMap, pageSearchParam.getQueryPrams());

        // 数据权限过滤
        if (request != null && getDataFilterService() != null) {
            String url = request.getServletPath(); // 获取请求访问路径
            String hasAuthSql = dataFilterService.getSql(url, systemParams);
            queryWrapper.and(wrapper -> {
                if ("1=0".equals(hasAuthSql)) {
                    this.extendDataFilterCondition(wrapper, systemParams);
                } else {
                    wrapper.apply(CommonConstant.SYMBOL_LEFT_BRACKET + hasAuthSql + CommonConstant.SYMBOL_RIGHT_BRACKET);
                    wrapper.or(wrap -> {
                        this.extendDataFilterCondition(wrap, systemParams);
                    }); // 数据权限过滤 与 数据权限过滤扩展 是or关系

                }
            });
        }

        // 解析排序规则
        addMultiSortFieldForQueryWrapper(queryWrapper, columnCacheMap, pageSearchParam.getSortField(), pageSearchParam.getSortOrder());

        IPage rtnPage = dao.selectPage(page, queryWrapper);
        return Result.success(rtnPage);
    }

    /**
     * 扩展数据权限过滤查询条件
     *
     * @param queryWrapper 查询对象
     * @param systemParams 系统参数
     * @return
     */
    @Override
    public void extendDataFilterCondition(QueryWrapper<T> queryWrapper, Map<String, Object> systemParams) {
        //设置默认的数据权限
        dataFilterService.setDefaultDataFilterCondition(queryWrapper, systemParams);
    }

    /**
     * ----->租户查询开始
     */

    private void addQueryWithTenantId(QueryWrapper<T> queryWrapper) {
        queryWrapper.and(wrapper -> {
            wrapper.eq("tenant_id", GlobalContext.getTenantId());
        });
    }

    private void addQueryWithTenantId(LambdaQueryWrapper<T> queryWrapper) {
        queryWrapper.and(wrapper -> {
            wrapper.eq(new SFunction<T, Object>() {

                /**
                 * Applies this function to the given argument.
                 *
                 * @param t the function argument
                 * @return the function result
                 */
                @Override
                public Object apply(T t) {
                    return "tenant_id";
                }
            }, GlobalContext.getTenantId());
        });
    }

    @Override
    public T selectOneWithTenantId(LambdaQueryWrapper<T> queryWrapper) {
        addQueryWithTenantId(queryWrapper);
        return (T) selectOne(queryWrapper);
    }


    @Override
    public T selectOneWithTenantId(QueryWrapper<T> queryWrapper) {
        addQueryWithTenantId(queryWrapper);
        return (T) selectOne(queryWrapper);
    }

    @Override
    public Long selectCountWithTenantId(QueryWrapper<T> queryWrapper) {
        addQueryWithTenantId(queryWrapper);
        return selectCount(queryWrapper);
    }

    @Override
    public List<T> selectListWithTenantId(QueryWrapper<T> queryWrapper) {
        addQueryWithTenantId(queryWrapper);
        return selectList(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> selectMapsWithTenantId(QueryWrapper<T> queryWrapper) {
        addQueryWithTenantId(queryWrapper);
        return selectMaps(queryWrapper);
    }

    @Override
    public IPage<T> selectPageWithTenantId(IPage page, QueryWrapper<T> queryWrapper) {
        addQueryWithTenantId(queryWrapper);
        return selectPage(page, queryWrapper);
    }

    /**
     * ----->租户查询结束
     */

    @Override
    public List<T> selectListByParam(Map<String, BaseQueryValue> queryPrams) {
        // 开始执行queryWrapper封装
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        // 查询参数封装
        parseQueryWrapperFromPageParam(queryWrapper, queryPrams);

        return dao.selectList(queryWrapper);
    }

    private Boolean isNull(Object obj) {
        if (obj == null) {
            return true;
        }
        String str = String.valueOf(obj);
        if (Strings.isEmpty(str)) {
            return true;
        }
        return false;
    }

    @Override
    public List<T> selectListByBaseParam(BaseSearchParam baseSearchParam) {
        // 开始执行queryWrapper封装
        QueryWrapper<T> queryWrapper = null;
        if (baseSearchParam.getWrapper() != null) {
            queryWrapper = baseSearchParam.getWrapper();
        } else {
            queryWrapper = new QueryWrapper<>();
        }

        // 查询参数封装
        parseQueryWrapperFromPageParam(queryWrapper, baseSearchParam.getQueryPrams(), baseSearchParam.getSortField(), baseSearchParam.getSortOrder());

        return dao.selectList(queryWrapper);
    }

    /**
     * 不带排序的解析查询条件
     *
     * @param queryWrapper
     * @param queryParams
     */
    private void parseQueryWrapperFromPageParam(QueryWrapper<T> queryWrapper, Map<String, BaseQueryValue> queryParams) {
        parseQueryWrapperFromPageParam(queryWrapper, queryParams, null, null);
    }

    /**
     * 带排序的解析查询条件
     *
     * @param queryWrapper
     * @param queryParams
     * @param sortField
     * @param orderBy
     */
    private void parseQueryWrapperFromPageParam(QueryWrapper<T> queryWrapper, Map<String, BaseQueryValue> queryParams, String sortField, String orderBy) {
        Map<String, ColumnCache> columnCacheMap = LambdaUtils.getColumnMap(GenericsUtils.getSuperClassGenricType(this.getClass(), 1));

        addQueryForQueryWrapper(queryWrapper, columnCacheMap, queryParams);

        addSortFieldForQueryWrapper(queryWrapper, columnCacheMap, sortField, orderBy);
    }

    /**
     * 添加排序，默认是asc
     *
     * @param queryWrapper
     * @param columnCacheMap
     * @param sortField
     * @param orderBy
     */
    private void addSortFieldForQueryWrapper(QueryWrapper<T> queryWrapper, Map<String, ColumnCache> columnCacheMap, String sortField, String orderBy) {
        // 构造排序内容
        if (StringUtils.isNotEmpty(sortField) && columnCacheMap != null && columnCacheMap.containsKey(sortField.toUpperCase())) {
            ColumnCache columnCache = columnCacheMap.get(sortField.toUpperCase());
            if (columnCache != null) {
                String column = columnCache.getColumn();
                if (StringUtils.isNotEmpty(column) && !column.equals(sortField)) {
                    sortField = column;
                }
                //默认倒序排列
                if ("desc".equalsIgnoreCase(orderBy)) {
                    queryWrapper.orderByDesc(sortField);
                } else {
                    queryWrapper.orderByAsc(sortField);
                }
            }
        }
    }

    /**
     * 添加多个排序规则，如果数量未一致，默认是以第一个排序规则处理。
     *
     * @param queryWrapper
     * @param columnCacheMap
     * @param sortField
     * @param orderBy
     */
    private void addMultiSortFieldForQueryWrapper(QueryWrapper<T> queryWrapper, Map<String, ColumnCache> columnCacheMap, String sortField, String orderBy) {
        if (StringUtils.isNotEmpty(sortField)) {
            String[] sortFields = sortField.split(",");
            String[] orderBys = new String[sortFields.length];
            if (StringUtils.isEmpty(orderBy)) {
                orderBy = "asc";
            }
            String[] orderByArray = orderBy.split(",");
            //当orderby参数不匹配的时候，使用第一个作为所有的跑徐规则
            if (orderByArray.length != sortFields.length) {
                for (String sort : sortFields) {
                    addSortFieldForQueryWrapper(queryWrapper, columnCacheMap, sort, orderByArray[0]);
                }
            } else {
                for (int i = 0, size = sortFields.length; i < size; i++) {
                    addSortFieldForQueryWrapper(queryWrapper, columnCacheMap, sortFields[i], orderByArray[i]);
                }
            }

        }
    }

    /**
     * 解析查询参数，封装queryWrapper查询条件
     *
     * @param queryWrapper
     * @param queryParams
     */
    private void addQueryForQueryWrapper(QueryWrapper<T> queryWrapper, Map<String, ColumnCache> columnCacheMap, Map<String, BaseQueryValue> queryParams) {
        for (Map.Entry<String, BaseQueryValue> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            if (StrUtil.isEmpty(key)) {
                continue;
            }
            try {
                String column = "";
                if (columnCacheMap != null) {
                    ColumnCache columnCache = columnCacheMap.get(key.toUpperCase());
                    if (columnCache != null) {
                        column = columnCache.getColumn();
                    }
                }
                if (StrUtil.isNotBlank(column) && !column.equals(key)) {
                    key = column;
                }
            } catch (Exception e) {
            }
            BaseQueryValue baseQueryValue = entry.getValue();
            // 查询操作符
            String opt = baseQueryValue.getOpt();
            if (StrUtil.isEmpty(opt)) {
                continue;
            }
            Object[] valueArr = baseQueryValue.getValues();
            if (valueArr == null || valueArr.length == 0) {
                continue;
            }
            // 执行判断生成wrapper
            switch (opt) {
                // 等于
                case SearchOptConstant.SEARCH_EQUAL:
                    queryWrapper.eq(key, valueArr[0]);
                    break;
                // 小于
                case SearchOptConstant.SEARCH_LESS_THEN:
                    queryWrapper.lt(key, valueArr[0]);
                    break;
                // 小于等于
                case SearchOptConstant.SEARCH_LESS_OR_EQUAL:
                    queryWrapper.le(key, valueArr[0]);
                    break;
                // 大于
                case SearchOptConstant.SEARCH_GREATER_THEN:
                    queryWrapper.gt(key, valueArr[0]);
                    break;
                // 大于等于
                case SearchOptConstant.SEARCH_GREATER_THEN_OR_EQUAL:
                    queryWrapper.ge(key, valueArr[0]);
                    break;
                // between
                case SearchOptConstant.SEARCH_BETWEEN:
                    if (valueArr != null && valueArr.length >= 2) {
                        // 左边空，右边有
                        if (this.isNull(valueArr[0]) && !this.isNull(valueArr[1])) {
                            queryWrapper.le(key, valueArr[1]);
                        }
                        // 左边有，右边空
                        else if (!this.isNull(valueArr[0]) && this.isNull(valueArr[1])) {
                            queryWrapper.ge(key, valueArr[0]);
                        }
                        // 两边有
                        else if (!this.isNull(valueArr[0]) && !this.isNull(valueArr[1])) {
                            if (String.valueOf(valueArr[0]).equals(String.valueOf(valueArr[1]))) {
                                queryWrapper.eq(key, valueArr[0]);
                            } else {
                                queryWrapper.between(key, valueArr[0], valueArr[1]);
                            }
                        }
                    }
                    break;
                // in
                case SearchOptConstant.SEARCH_IN:
                    queryWrapper.in(key, valueArr);
                    break;
                //like
                case SearchOptConstant.SEARCH_LIKE_BOTH:
                    queryWrapper.like(key, valueArr[0]);
                    break;
                //left like
                case SearchOptConstant.SEARCH_LIKE_LEFT:
                    queryWrapper.likeLeft(key, valueArr[0]);
                    break;
                // right like
                case SearchOptConstant.SEARCH_LIKE_RIGHT:
                    queryWrapper.likeRight(key, valueArr[0]);
                    break;
                // is not null
                case SearchOptConstant.SEARCH_IS_NOT_NULL:
                    queryWrapper.isNotNull(key);
                    break;
                // is null
                case SearchOptConstant.SEARCH_IS_NULL:
                    queryWrapper.isNull(key);
                    break;
                // 多字段左右模糊匹配
                case SearchOptConstant.SEARCH_LIKE_MULTILIKE:
                    //多字段对key逗号分隔处理处理
                    String[] keys = key.split(",");
                    queryWrapper.and(wrapper -> {
                        for (String k : keys) {
                            wrapper.or().like(k, valueArr[0]);
                        }
                    });
                    break;
                // 年月日时分秒between，前后包含,截止时间会转换成'2020-01-01 23:59:59.999'
                case SearchOptConstant.SERACH_DATETIME_BETWEEN:
                    if (valueArr != null && valueArr.length >= 2) {
                        // 左边空，右边有
                        if (this.isNull(valueArr[0]) && !this.isNull(valueArr[1])) {
                            queryWrapper.le(key, getEndDateOfDay(String.valueOf(valueArr[1])));
                        }
                        // 左边有，右边空
                        else if (!this.isNull(valueArr[0]) && this.isNull(valueArr[1])) {
                            queryWrapper.ge(key, getBeginDateOfDay(String.valueOf(valueArr[0])));
                        }
                        // 两边有
                        else if (!this.isNull(valueArr[0]) && !this.isNull(valueArr[1])) {
                            queryWrapper.between(key, getBeginDateOfDay(String.valueOf(valueArr[0])), getEndDateOfDay(String.valueOf(valueArr[1])));
                        }
                    }
                    break;
                default:
                    System.err.println("【" + key + "查询的操作方式未定义，本次查询自动跳过!】");
                    break;
            }
        }
    }

    /**
     * 获取指定天的开始时间，支持格式'yyyy-MM-dd'
     *
     * @param dateString
     * @return
     */
    private Date getBeginDateOfDay(String dateString) {
        DateTime endOfDay = DateUtil.parse(dateString);
        return DateUtil.beginOfDay(endOfDay);
    }

    /**
     * 获取指定天的结束时间，支持格式'yyyy-MM-dd'
     *
     * @param dateString
     * @return
     */
    private Date getEndDateOfDay(String dateString) {
        DateTime endOfDay = DateUtil.parse(dateString);
        return DateUtil.endOfDay(endOfDay);
    }
}

package pro.wuan.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.constant.ApplicationUtil;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.common.redis.util.JedisUtil;
import pro.wuan.constant.DictConstant;
import pro.wuan.dict.mapper.SysDictionaryMapper;
import pro.wuan.dict.service.ISysDictionaryService;
import pro.wuan.feignapi.dictapi.entity.SysDictionary;
import pro.wuan.feignapi.dictapi.model.DictParams;
import pro.wuan.utils.CommonUtil;
import pro.wuan.utils.DictUtil;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 数据字典业务实现
 *
 * @author: oldone
 * @date: 2021/9/7 16:02
 */
@Slf4j
@Service("sysDictionaryServiceImpl")
public class SysDictionaryServiceImpl extends BaseServiceImpl<SysDictionaryMapper, SysDictionary> implements ISysDictionaryService {
    @Autowired
    private JedisUtil jedisUtil;

    /**
     * 保存或修改数据字典或字典项
     *
     * @param sysDictionary
     * @return
     */
    public Boolean addOrUpdate(SysDictionary sysDictionary) {
        try {
            //判断是新增还是修改
            Boolean isAdd = sysDictionary.getId() == null ? true : false;
            if (sysDictionary.getSort() == null) {
                sysDictionary.setSort(CommonConstant.DEFAULT_SORT);
            }
            Boolean flag;
            if (isAdd) {
                flag = this.insert(sysDictionary) > 0;
            } else {
                flag = this.updateById(sysDictionary) > 0;
                sysDictionary = this.selectById(sysDictionary.getId());
            }
            if (flag) {
                //更新redis缓存
                DictUtil.updateDictItem(sysDictionary);
            }
        } catch (Exception e) {
            log.error("dictService addOrUpdate error:" + e);
            return false;
        }
        return true;
    }

    /**
     * 根据code删除字典或字典项
     *
     * @param id
     * @return
     */
    public Boolean deleteDictById(Long id) {
        try {
            SysDictionary dict = this.selectById(id);
            if (dict == null) {
                dict = DictUtil.getDictById(id);
                if (dict == null) {
                    return false;
                }
                //更新缓存
                DictUtil.updateDictItem(dict);
            }
            Boolean flag = this.deleteById(dict.getId()) > 0;
            if (flag) {
                dict.setDeleteFlag(ApplicationUtil.DeletedType.DELETED);
                //更新缓存
                DictUtil.updateDictItem(dict);
            }
            return flag;
        } catch (Exception e) {
            log.error("dictService deleteDict error:" + e);
            return false;
        }
    }


    /**
     * 批量删除字典或字典项
     *
     * @param ids
     * @return
     */
    public Boolean deleteDictBatchIds(String ids) {
        try {
            String[] codeArr = ids.split(",");
            if (codeArr.length < 1) {
                return false;
            }
            SysDictionary dict = this.selectById(Long.valueOf(codeArr[0]));
            if (dict == null) {
                return false;
            }
            List<Long> dictIds = Arrays.asList(codeArr).stream().map(y -> Long.valueOf(y)).collect(Collectors.toList());
            Boolean flag = this.deleteBatchIds(dictIds) > 0;
            if (flag) {
                //字典批量删除
                DictUtil.deleteBatch(dictIds, dict.getParentId());
            }
            return flag;
        } catch (Exception e) {
            log.error("dictService deleteDictBatchIds error:" + e);
            return false;
        }
    }

    /**
     * 从DB根据code获取字典
     *
     * @param code
     * @return
     */
    private SysDictionary getDictByCode(String code) {
        QueryWrapper<SysDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        return this.selectOne(queryWrapper);
    }


    /**
     * 根据父id获取字典项集合
     *
     * @param dictParams
     * @return 字典项集合
     */
    public List<SysDictionary> getByParentId(DictParams dictParams) {
        if (dictParams == null || dictParams.getParentId() == null) {
            return new ArrayList<>();
        }
        //父节点
        SysDictionary parentDict = DictUtil.getDictById(dictParams.getParentId());
        if (parentDict == null) {
            return new ArrayList<>();
        }
        List<SysDictionary> items = parentDict.getDictItems();
        //判断redis缓存是否存在数据，不存在则读取数据库
        if (CollectionUtils.isEmpty(items)) {
            items = this.getDbByParentId(dictParams.getParentId(), true);
        }
        return this.sortList(this.filerByParams(items, dictParams), dictParams);
    }

    /**
     * 根据父id从DB获取字典项集合
     *
     * @param parentId
     * @return
     */
    private List<SysDictionary> getDbByParentId(Long parentId, Boolean hasChildren) {
        if (parentId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<SysDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        queryWrapper.orderByAsc("sort");
        if (hasChildren) {
            return this.getDictList(queryWrapper);
        } else {
            return this.selectList(queryWrapper);
        }
    }

    /**
     * 参数查询过滤
     *
     * @param dicts
     * @param dictParams
     * @return
     */
    private List<SysDictionary> filerByParams(List<SysDictionary> dicts, DictParams dictParams) {
        List<SysDictionary> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(dicts) || dictParams == null) {
            return dicts;
        }
        //查询条件为空直接返回
        if (StringUtils.isEmpty(dictParams.getDisplayFlag()) && StringUtils.isEmpty(dictParams.getName()) && StringUtils.isEmpty(dictParams.getValue())) {
            return dicts;
        }

        Predicate<SysDictionary> predicate = y -> true;
        if (StringUtils.isNotEmpty(dictParams.getDisplayFlag())) {
            predicate = predicate.and(y -> Boolean.valueOf(dictParams.getDisplayFlag()) ? y.isDisplayFlag() : !y.isDisplayFlag());
        }
        if (StringUtils.isNotEmpty(dictParams.getName())) {
            predicate = predicate.and(y -> y.getName().contains(dictParams.getName()));
        }
        if (StringUtils.isNotEmpty(dictParams.getValue())) {
            predicate = predicate.and(y -> y.getValue().contains(dictParams.getValue()));
        }
        result.addAll(dicts.stream().filter(predicate).collect(Collectors.toList()));
        dicts.stream().forEach(d -> {
            List<SysDictionary> returnDicts = this.filerByParams(d.getDictItems(), dictParams);
            if (CollectionUtils.isNotEmpty(returnDicts)) {
                result.addAll(returnDicts);
            }
        });
        return result;
    }

    /**
     * 对列表进行排序
     *
     * @param dicts
     * @return
     */
    private List<SysDictionary> sortList(List<SysDictionary> dicts, DictParams dictParams) {
        if (CollectionUtils.isNotEmpty(dicts)) {
            if (dictParams != null && Strings.isNotEmpty(dictParams.getSortField())) {
                if (dictParams.getSortField().equals("sort")) {
                    if ("desc".equals(dictParams.getSortOrder())) {
                        //先按sort倒序,再按创建时间倒排
                        dicts = dicts.stream().sorted(Comparator.comparing(SysDictionary::getSort, Comparator.reverseOrder()).
                                thenComparing(SysDictionary::getCreateTime, Comparator.reverseOrder())).collect(Collectors.toList());
                    } else {
                        //先按sort升序,再按创建时间倒排
                        dicts = dicts.stream().sorted(Comparator.comparing(SysDictionary::getSort).
                                thenComparing(SysDictionary::getCreateTime, Comparator.reverseOrder())).collect(Collectors.toList());
                    }
                } else if (dictParams.getSortField().equals("value")) {
                    if ("desc".equals(dictParams.getSortOrder())) {
                        //先按value倒序,再按创建时间倒排
                        dicts = dicts.stream().sorted(Comparator.comparing(SysDictionary::getValue, Comparator.reverseOrder()).
                                thenComparing(SysDictionary::getCreateTime, Comparator.reverseOrder())).collect(Collectors.toList());
                    } else {
                        //先按value升序,再按创建时间倒排
                        dicts = dicts.stream().sorted(Comparator.comparing(SysDictionary::getValue).
                                thenComparing(SysDictionary::getCreateTime, Comparator.reverseOrder())).collect(Collectors.toList());
                    }
                }

            } else {
                //先安sort升序,再按创建时间倒排
                dicts = dicts.stream().sorted(Comparator.comparing(SysDictionary::getSort).
                        thenComparing(SysDictionary::getCreateTime, Comparator.reverseOrder())).collect(Collectors.toList());
            }
        }
        return dicts;
    }

    /**
     * 获取字典类型子类型
     *
     * @param parentIds
     * @return 字典项map
     */
    public Map<Long, List<SysDictionary>> getMapByParentIds(List<Long> parentIds) {
        if (CollectionUtils.isEmpty(parentIds)) {
            return new HashMap<>();
        }
        QueryWrapper<SysDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("parent_id", parentIds);
        queryWrapper.eq("catalogue_flag", true);
        List<SysDictionary> itemList = this.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(itemList)) {
            return new HashMap<>();
        }
        return this.getMap(itemList);
    }


    /**
     * 根据应用id获取字典类型
     *
     * @param appId       应用id
     * @param hasChildren 是否带子节点
     * @return
     */
    public List<SysDictionary> getByAppId(Long appId, Boolean hasChildren) {
        if (appId == null) {
            return new ArrayList<>();
        }
        List<SysDictionary> dicts = DictUtil.getItemsByAppId(appId, hasChildren);
        if (CollectionUtils.isEmpty(dicts)) {
            QueryWrapper<SysDictionary> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_id", appId);
            queryWrapper.eq("parent_id", CommonConstant.DEFAULT_PARENT_ID);
            queryWrapper.orderByAsc("sort");
            dicts = this.selectList(queryWrapper);
        }
        if (CollectionUtils.isEmpty(dicts)) {
            return new ArrayList<>();
        }
        //判断是否需要带子节点
        if (hasChildren) {
            List<Long> ids = dicts.stream().map(y -> y.getId()).collect(Collectors.toList());
            Map<Long, List<SysDictionary>> dictMap = this.getMapByParentIds(ids);
            dicts.stream().forEach(y -> {
                if (dictMap == null) {
                    y.setHasChildren(false);
                } else {
                    y.setHasChildren(CollectionUtils.isNotEmpty(dictMap.get(y.getId())));
                    y.setDictItems(dictMap.get(y.getId()));
                }

            });
        }
        return this.sortList(dicts, null);
    }

    /**
     * 根据id获取字典
     *
     * @param id
     * @return
     */
    public SysDictionary getById(Long id) {
        if (id == null) {
            return null;
        }
        SysDictionary dict = DictUtil.getDictById(id);
        //判断缓存中是否取到
        if (dict == null) {
            dict = this.selectById(id);
        }
        return dict;
    }

    /**
     * 根据字典编码和字典项值获取字典名称
     *
     * @param code
     * @param val
     * @return
     */
    public String getDictNameByCodeAndVal(String code, String val) {
        QueryWrapper<SysDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        queryWrapper.eq("catalogue_flag", false);
        queryWrapper.eq("value", val);
        queryWrapper.last("limit 1");
        SysDictionary dict = this.selectOne(queryWrapper);
        if (dict == null) {
            return Strings.EMPTY;
        }
        return dict.getName();
    }

    /**
     * 根据字典编码和字典名称获取字典值
     *
     * @param code
     * @param name
     * @return
     */
    public String getDictValByNameAndCode(String code, String name) {
        QueryWrapper<SysDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        queryWrapper.eq("catalogue_flag", false);
        queryWrapper.eq("name", name);
        queryWrapper.last("limit 1");
        SysDictionary dict = this.selectOne(queryWrapper);
        if (dict == null) {
            return Strings.EMPTY;
        }
        return dict.getValue();
    }

    /**
     * 获取所有字典及字典项
     *
     * @return
     */
    public List<SysDictionary> getAllDict() {
        QueryWrapper<SysDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", CommonConstant.DEFAULT_PARENT_ID);
        return this.getDictList(queryWrapper);
    }

    /**
     * 根据查询条件获取字典及字典项集合
     *
     * @param queryWrapper
     * @return
     */
    private List<SysDictionary> getDictList(QueryWrapper<SysDictionary> queryWrapper) {
        List<SysDictionary> dicts = this.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(dicts)) {
            return new ArrayList<>();
        }
        //字典项父id集合
        List<Long> parentIds = dicts.stream().map(y -> y.getId()).collect(Collectors.toList());
        Map<Long, List<SysDictionary>> dictMap = this.getMapByIds(parentIds);
        if (CollectionUtils.isNotEmpty(dictMap)) {
            //遍历设置每个字典的字典项
            dicts.stream().forEach(d -> {
                d.setDictItems(dictMap.get(d.getId()));
            });
        }
        return dicts;
    }

    /**
     * 获取字典项集合及子项
     *
     * @param parentIds
     * @return
     */
    private List<SysDictionary> getItemList(List<Long> parentIds) {
        List<SysDictionary> rtnList = new ArrayList<>();
        //分批处理
        List<List<Long>> partsChange = CommonUtil.averageAssignByPage(parentIds, 200);
        for(List<Long> onePageChange:partsChange){
            QueryWrapper<SysDictionary> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.in("parent_id", onePageChange);
            List<SysDictionary> itemList = this.selectList(queryWrapper1);
            if (CollectionUtils.isEmpty(itemList)) {
                continue;
            }
            //字典项父id集合
            List<Long> ids = itemList.stream().map(y -> y.getId()).collect(Collectors.toList());
            //进行递归获取子项
            Map<Long, List<SysDictionary>> dictMap = this.getMapByIds(ids);
            if (CollectionUtils.isNotEmpty(dictMap)) {
                //遍历设置每个字典的字典项
                itemList.stream().forEach(d -> {
                    d.setDictItems(dictMap.get(d.getId()));
                });
            }
            rtnList.addAll(itemList);
        }


        return rtnList;
    }

    /**
     * 获取字典项map
     *
     * @param items
     * @return
     */
    private Map<Long, List<SysDictionary>> getMap(List<SysDictionary> items) {
        //判断字典项集合是否为空
        if (CollectionUtils.isEmpty(items)) {
            return new HashMap<>();
        }
        Map<Long, List<SysDictionary>> dictMap = items.stream().collect(Collectors.groupingBy(SysDictionary::getParentId, Collectors.toList()));
        //对每个字典项进行组内排序
        for (Map.Entry<Long, List<SysDictionary>> entry : dictMap.entrySet()) {
            List<SysDictionary> value = entry.getValue();
            dictMap.put(entry.getKey(), value.stream().sorted(Comparator.comparing(y -> y.getSort())).collect(Collectors.toList()));
        }
        return dictMap;
    }

    /**
     * 获取字典项map
     *
     * @param parentIds
     * @return
     */
    private Map<Long, List<SysDictionary>> getMapByIds(List<Long> parentIds) {
        return this.getMap(this.getItemList(parentIds));
    }

    /**
     * 判断是否有子节点
     *
     * @param id
     * @return
     */
    public Boolean hasChildren(Long id) {
        QueryWrapper<SysDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        List<SysDictionary> items = this.selectList(queryWrapper);
        if (items != null && CollectionUtils.isNotEmpty(items)) {
            return true;
        }
        return false;
    }

    /**
     * 判断批量删除是否有子节点
     *
     * @param ids
     * @return
     */
    public Boolean hasChildrenByIds(String ids) {
        String[] idArr = ids.split(",");
        QueryWrapper<SysDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("parent_id", Arrays.asList(idArr));
        List<SysDictionary> items = this.selectList(queryWrapper);
        if (items != null && CollectionUtils.isNotEmpty(items)) {
            return true;
        }
        return false;
    }

    /**
     * 数据字典控件绑定
     *
     * @param code 字典编码
     * @return 字典项集合
     */
    @Override
    public List<SysDictionary> bindDict(String code) {
        if (Strings.isEmpty(code)) {
            return new ArrayList<>();
        }
        SysDictionary parentDict = DictUtil.getDictByCode(code);
        List<SysDictionary> items=new ArrayList<>();
        if(parentDict!=null){
            items=parentDict.getDictItems();
        }
        //判断redis缓存是否存在数据，不存在则读取数据库
        if (CollectionUtils.isEmpty(items)) {
            QueryWrapper<SysDictionary> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", code);
            queryWrapper.eq("catalogue_flag", true);
            SysDictionary parentDict2 = this.selectOne(queryWrapper);
            if (parentDict2 == null) {
                return new ArrayList<>();
            }
            items = this.getDbByParentId(parentDict2.getId(), false);
        }
        items = items.stream().filter(y -> y.isDisplayFlag()).map(y -> {
            SysDictionary dict = new SysDictionary();
            dict.setId(y.getId());
            dict.setName(y.getName());
            dict.setValue(y.getValue());
            return dict;
        }).collect(Collectors.toList());
        //按sort排序
       // items = items.stream().filter(f -> f.getSort() != null).sorted(Comparator.comparing(SysDictionary::getSort)).collect(Collectors.toList());
        return items;
    }


    /**
     * 树形字典控件绑定
     *
     * @param parentId 父节点id
     * @return 字典项集合
     */
    public List<SysDictionary> bindDictByParentId(Long parentId) {
        if (parentId == null) {
            return new ArrayList<>();
        }
        SysDictionary parentDict = DictUtil.getDictById(parentId);
        if (parentDict == null) {
            return new ArrayList<>();
        }
        List<SysDictionary> items = parentDict.getDictItems();
        //判断redis缓存是否存在数据，不存在则读取数据库
        if (CollectionUtils.isEmpty(items)) {
            items = this.getDbByParentId(parentId, false);
        }
        items = items.stream().filter(y -> y.isDisplayFlag()).map(y -> {
            SysDictionary dict = new SysDictionary();
            dict.setId(y.getId());
            dict.setName(y.getName());
            dict.setValue(y.getValue());
            return dict;
        }).collect(Collectors.toList());
        return items;
    }

    /**
     * 判断目录code是否已存在
     *
     * @param id   修改时传的主键id
     * @param code 字典编码
     * @return
     */
    public Boolean isExistsByCode(Long id, String code) {
        LambdaQueryWrapper<SysDictionary> queryWrapper = new LambdaQueryWrapper<>();
        if (id != null) {
            queryWrapper.ne(SysDictionary::getId, id);
        }
        queryWrapper.eq(SysDictionary::getCode, code);
        queryWrapper.eq(SysDictionary::isCatalogueFlag, true);
        return this.count(queryWrapper) > 0;
    }


    /**
     * 判断同一字典项下的value值是否已存在
     *
     * @param id    修改时传的主键id
     * @param code  字典编码
     * @param value 字典值
     * @return
     */
    public Boolean isExistsByValue(Long id, String code, String value) {
        LambdaQueryWrapper<SysDictionary> queryWrapper = new LambdaQueryWrapper<>();
        if (id != null) {
            queryWrapper.ne(SysDictionary::getId, id);
        }
        queryWrapper.eq(SysDictionary::getCode, code);
        queryWrapper.eq(SysDictionary::isCatalogueFlag, false);
        queryWrapper.eq(SysDictionary::getValue, value);
        return this.count(queryWrapper) > 0;
    }


    /**
     * 缓存刷新
     */
    public void refreashCache() {
        //读取所有字典项
        List<SysDictionary> dicts = this.getAllDict();
        if (CollectionUtils.isNotEmpty(dicts)) {
            jedisUtil.set(DictConstant.ALL_DICT_KEY, dicts);
        }
    }

}

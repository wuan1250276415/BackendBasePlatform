package pro.wuan.utils;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.redis.util.JedisUtil;
import pro.wuan.constant.DictConstant;
import pro.wuan.feignapi.dictapi.entity.SysDictionary;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据字典工具类
 *
 * @author: oldone
 * @date: 2021/9/8 9:48
 */
@Slf4j
@Component
public class DictUtil {

    @Autowired
    JedisUtil jedisUtil;

    private static DictUtil dictUtil;

    @PostConstruct
    public void initDict() {
        dictUtil = this;
        dictUtil.jedisUtil = this.jedisUtil;
    }

    /**
     * 根据字典编码获取字典项
     *
     * @param code
     * @return
     */
    public static SysDictionary getDictByCode(String code) {
        try {
            Object obj = dictUtil.jedisUtil.get(DictConstant.ALL_DICT_KEY);
            if (obj == null) {
                return null;
            }
            List<SysDictionary> dicts = (List<SysDictionary>) obj;
            if (CollectionUtils.isEmpty(dicts)) {
                return null;
            }
            SysDictionary dict = DictUtil.getDictByCode(dicts, code);
            return dict;
        } catch (Exception e) {
            log.error("DictUtil getDictByCode error:" + e);
            return null;
        }
    }

    /**
     * 根据字典编码获取字典项
     *
     * @param dicts
     * @param code
     * @return
     */
    public static SysDictionary getDictByCode(List<SysDictionary> dicts, String code) {
        SysDictionary result = null;
        if (CollectionUtils.isEmpty(dicts) || Strings.isEmpty(code)) {
            return result;
        }
        List<SysDictionary> filterDicts = dicts.stream().filter(y -> Strings.isNotEmpty(y.getCode()) && y.getCode().equals(code) && y.isCatalogueFlag()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterDicts)) {
            for (SysDictionary dict : dicts) {
                result = DictUtil.getDictByCode(dict.getDictItems(), code);
                if (result != null) {
                    break;
                }
            }
        } else {
            result = filterDicts.get(0);
        }
        return result;
    }


    /**
     * 根据字典id获取字典项
     *
     * @param id
     * @return
     */
    public static SysDictionary getDictById(Long id) {
        try {
            Object obj = dictUtil.jedisUtil.get(DictConstant.ALL_DICT_KEY);
            if (obj == null) {
                return null;
            }
            List<SysDictionary> dicts = (List<SysDictionary>) obj;
            if (CollectionUtils.isEmpty(dicts)) {
                return null;
            }
            SysDictionary dict = DictUtil.getDictById(dicts, id);
            return dict;
        } catch (Exception e) {
            log.error("DictUtil getDictById error:" + e);
            return null;
        }
    }

    /**
     * 根据字典id获取字典项
     *
     * @param dicts
     * @param id
     * @return
     */
    public static SysDictionary getDictById(List<SysDictionary> dicts, Long id) {
        SysDictionary result = null;
        if (CollectionUtils.isEmpty(dicts) || id == null) {
            return result;
        }
        List<SysDictionary> filterDicts = dicts.stream().filter(y -> y.getId().equals(id)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterDicts)) {
            for (SysDictionary dict : dicts) {
                result = DictUtil.getDictById(dict.getDictItems(), id);
                if (result != null) {
                    break;
                }
            }
        } else {
            result = filterDicts.get(0);
        }
        return result;
    }


    /**
     * 根据所属应用获取下面字典目录
     *
     * @param appId
     * @return
     */
    public static List<SysDictionary> getItemsByAppId(Long appId, Boolean hasChildren) {
        try {
            Object obj = dictUtil.jedisUtil.get(DictConstant.ALL_DICT_KEY);
            if (obj == null) {
                return new ArrayList<>();
            }
            List<SysDictionary> dicts = (List<SysDictionary>) obj;
            if (CollectionUtils.isEmpty(dicts)) {
                return new ArrayList<>();
            }
            List<SysDictionary> filerDicts = dicts.stream().filter(y -> appId.equals(y.getAppId()) && CommonConstant.DEFAULT_PARENT_ID.equals(y.getParentId())).collect(Collectors.toList());
            filerDicts.stream().forEach(y -> {
                if (!hasChildren || CollectionUtils.isEmpty(y.getDictItems())) {
                    y.setDictItems(null);
                    y.setHasChildren(false);
                } else {
                    //获取子字典目录
                    List<SysDictionary> typeItems = y.getDictItems().stream().filter(s -> s.isCatalogueFlag()).collect(Collectors.toList());
                    y.setHasChildren(CollectionUtils.isNotEmpty(typeItems));
                    y.setDictItems(typeItems);
                }
            });
            return filerDicts;
        } catch (Exception e) {
            log.error("DictUtil getItemsByAppId error:" + e);
            return new ArrayList<>();
        }
    }


//    /**
//     * 根据字典编码集合获取下面字典map
//     *
//     * @param parentCodes
//     * @return
//     */
//    public static Map<String, List<SysDictionary>> getMapByParentCodes(List<String> parentCodes) {
//        try {
//            List<Object> dictObjs = dictUtil.jedisUtil.mutilGet(parentCodes);
//            if (CollectionUtils.isEmpty(dictObjs)) {
//                return new HashMap<>();
//            }
//            List<SysDictionary> dicts = dictObjs.stream().map(m -> (SysDictionary) m).collect(Collectors.toList());
//            Map<String, List<SysDictionary>> dictMap = dicts.stream().collect(Collectors.groupingBy(SysDictionary::getParentCode, Collectors.toList()));
//            //对每个字典项进行组内排序
//            for (Map.Entry<String, List<SysDictionary>> entry : dictMap.entrySet()) {
//                List<SysDictionary> value = entry.getValue();
//                dictMap.put(entry.getKey(), value.stream().sorted(Comparator.comparing(y -> y.getSort())).collect(Collectors.toList()));
//            }
//            return dicts.stream().collect(Collectors.groupingBy(SysDictionary::getParentCode, Collectors.toList()));
//        } catch (Exception e) {
//            log.error("DictUtil getMapByParentCodes error2:" + e);
//            return new HashMap<>();
//        }
//    }

    /**
     * 更新字典项
     *
     * @param dict 待更新的字典链路
     */
    public static void updateDictItem(SysDictionary dict) {
        Object obj = dictUtil.jedisUtil.get(DictConstant.ALL_DICT_KEY);
        if (obj == null) {
            dictUtil.jedisUtil.set(DictConstant.ALL_DICT_KEY, new ArrayList() {{
                add(dict);
            }});
            return;
        }
        List<SysDictionary> dicts = (List<SysDictionary>) obj;
        if (CollectionUtils.isEmpty(dicts)) {
            dictUtil.jedisUtil.set(DictConstant.ALL_DICT_KEY, new ArrayList() {{
                add(dict);
            }});
            return;
        }
        if (dict == null) {
            return;
        }
        if (CommonConstant.DEFAULT_PARENT_ID.equals(dict.getParentId())) {
            List<SysDictionary> deleteDicts = dicts.stream().filter(d -> d.getId().equals(dict.getId())).collect(Collectors.toList());
            dicts.removeAll(deleteDicts);
            if (!dict.isDeleteFlag()) {
                if (CollectionUtils.isNotEmpty(deleteDicts)) {
                    dict.setDictItems(deleteDicts.get(0).getDictItems());
                }
                dicts.add(dict);
            }
        } else {
            DictUtil.updateDictByList(dicts, dict);
        }
        dictUtil.jedisUtil.set(DictConstant.ALL_DICT_KEY, new ArrayList<>(dicts));
    }

    /**
     * 递归更新目标数据字典或字典项
     *
     * @param dicts
     * @param dict
     */
    public static Boolean updateDictByList(List<SysDictionary> dicts, SysDictionary dict) {
        Boolean flag = false;
        if (CollectionUtils.isEmpty(dicts)) {
            return flag;
        }
        for (SysDictionary item : dicts) {
            if (item.getId().equals(dict.getParentId())) {
                flag = true;
                if (CollectionUtils.isNotEmpty(item.getDictItems())) {
                    List<SysDictionary> deleteDicts = item.getDictItems().stream().filter(d -> d.getId().equals(dict.getId())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(deleteDicts)) {
                        item.getDictItems().removeAll(deleteDicts);
                        dict.setDictItems(deleteDicts.get(0).getDictItems());
                    }
                    if (!dict.isDeleteFlag()) {
                        item.getDictItems().add(dict);
                    }
                } else {
                    List<SysDictionary> items = new ArrayList<>();
                    items.add(dict);
                    item.setDictItems(items);
                }
                break;
            }
            flag = DictUtil.updateDictByList(item.getDictItems(), dict);
        }
        return flag;
    }

    /**
     * 批量删除字典及字典项
     *
     * @param deleteIds
     * @param parentId
     */
    public static void deleteBatch(List<Long> deleteIds, Long parentId) {
        if (CollectionUtils.isEmpty(deleteIds) || parentId == null) {
            return;
        }

        Object obj = dictUtil.jedisUtil.get(DictConstant.ALL_DICT_KEY);
        if (obj == null) {
            return;
        }
        List<SysDictionary> dicts = (List<SysDictionary>) obj;
        DictUtil.deleteBatchByItem(dicts, deleteIds, parentId);
        dictUtil.jedisUtil.set(DictConstant.ALL_DICT_KEY, new ArrayList<>(dicts));
    }

    /**
     * 批量删除字典及字典项
     *
     * @param dicts
     * @param deleteIds
     */
    public static void deleteBatchByItem(List<SysDictionary> dicts, List<Long> deleteIds, Long parentId) {
        if (CollectionUtils.isEmpty(dicts) || CollectionUtils.isEmpty(deleteIds) || parentId == null) {
            return;
        }

        for (SysDictionary item : dicts) {
            if (item.getId().equals(parentId)) {
                List<SysDictionary> deleteDicts = item.getDictItems().stream().filter(y -> deleteIds.stream().anyMatch(d -> d.equals(y.getId()))).collect(Collectors.toList());
                item.getDictItems().removeAll(deleteDicts);
                break;
            } else {
                DictUtil.deleteBatchByItem(item.getDictItems(), deleteIds, parentId);
            }
        }
    }
}

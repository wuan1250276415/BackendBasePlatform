package pro.wuan.dict.service;


import pro.wuan.common.core.service.IBaseService;
import pro.wuan.dict.mapper.SysDictionaryMapper;
import pro.wuan.feignapi.dictapi.entity.SysDictionary;
import pro.wuan.feignapi.dictapi.model.DictParams;

import java.util.List;
import java.util.Map;

/**
 * 数据字典接口
 *
 * @author: oldone
 * @date: 2021/9/7 15:59
 */
public interface ISysDictionaryService extends IBaseService<SysDictionaryMapper, SysDictionary> {

    /**
     * 保存或修改数据字典或字典项
     *
     * @param sysDictionary
     * @return
     */
    Boolean addOrUpdate(SysDictionary sysDictionary);

    /**
     * 根据id删除字典或字典项
     *
     * @param id
     * @return
     */
    Boolean deleteDictById(Long id);

    /**
     * 批量删除字典或字典项
     *
     * @param ids
     * @return
     */
    Boolean deleteDictBatchIds(String ids);

    /**
     * 根据id获取字典
     *
     * @param id
     * @return
     */
    SysDictionary getById(Long id);

    /**
     * 根据父code获取字典项集合
     *
     * @param dictParams
     * @return 字典项集合
     */
    List<SysDictionary> getByParentId(DictParams dictParams);

    /**
     * 根据父id集合获取字典项map
     *
     * @param ids
     * @return 字典项map
     */
    Map<Long, List<SysDictionary>> getMapByParentIds(List<Long> ids);

    /**
     * 根据应用id获取字典类型
     *
     * @param appId 应用id
     * @return
     */
    List<SysDictionary> getByAppId(Long appId, Boolean hasChildren);

    /**
     * 根据字典编码和字典项值获取字典名称
     *
     * @param code
     * @param val
     * @return
     */
    String getDictNameByCodeAndVal(String code, String val);

    /**
     * 根据字典编码和字典名称获取字典值
     *
     * @param code
     * @param name
     * @return
     */
    String getDictValByNameAndCode(String code, String name);

    /**
     * 获取所有字典及字典项
     *
     * @return
     */
    List<SysDictionary> getAllDict();

    /**
     * 判断是否有子节点
     *
     * @param id
     * @return
     */
    Boolean hasChildren(Long id);

    /**
     * 判断批量删除是否有子节点
     *
     * @param ids
     * @return
     */
    Boolean hasChildrenByIds(String ids);


    /**
     * 数据字典控件绑定
     *
     * @param code 字典编码
     * @return 字典项集合
     */
    List<SysDictionary> bindDict(String code);

    /**
     * 树形字典控件绑定
     *
     * @param parentId 父节点id
     * @return 字典项集合
     */
    List<SysDictionary> bindDictByParentId(Long parentId);

    /**
     * 判断目录code是否已存在
     *
     * @param id   修改时传的主键id
     * @param code 字典编码
     * @return
     */
    Boolean isExistsByCode(Long id, String code);

    /**
     * 判断同一字典项下的value值是否已存在
     *
     * @param id    修改时传的主键id
     * @param code  字典编码
     * @param value 字典值
     * @return
     */
    Boolean isExistsByValue(Long id, String code, String value);

    /**
     * 缓存刷新
     */
    void refreashCache();
}

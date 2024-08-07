package pro.wuan.dict.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.dictapi.entity.SysDictionary;

/** 数据字段mapper层
 * @author: oldone
 * @date: 2021/9/7 15:22
 */
@Mapper
public interface SysDictionaryMapper extends IBaseMapper<SysDictionary> {
}

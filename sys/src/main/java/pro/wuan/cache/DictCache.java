package pro.wuan.cache;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.wuan.common.redis.util.JedisUtil;
import pro.wuan.constant.DictConstant;
import pro.wuan.dict.service.ISysDictionaryService;
import pro.wuan.feignapi.dictapi.entity.SysDictionary;

import java.util.List;


/**
 * 项目启动时数据字典缓存
 *
 * @author: oldone
 * @date: 2021/9/7 17:05
 */
@Component
public class DictCache {
    @Autowired
    private ISysDictionaryService dictionaryService;
    @Autowired
    private JedisUtil jedisUtil;

    @PostConstruct
    public void initDictCache() {
        //读取所有字典项
        List<SysDictionary> dicts = dictionaryService.getAllDict();
        if (CollectionUtils.isNotEmpty(dicts)) {
            jedisUtil.set(DictConstant.ALL_DICT_KEY, dicts);
        }

    }
}

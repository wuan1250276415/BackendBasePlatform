package pro.wuan.feignapi.dictapi.fallback;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.feignapi.dictapi.entity.SysDictionary;
import pro.wuan.feignapi.dictapi.feign.DictFeignClient;
import pro.wuan.feignapi.dictapi.model.DictParams;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典接口降级服务
 *
 * @author: oldone
 * @date: 2021/9/8 10:59
 */
@Component
public class DictFeignClientFallback implements DictFeignClient {


    @Override
    public List<SysDictionary> getListByParams(DictParams dictParams) {
        return new ArrayList<>();
    }

    @Override
    public List<SysDictionary> getListByParentCode(String parentCode) {
        return new ArrayList<>();
    }

    @Override
    public String getDictNameByCodeAndVal(@RequestParam("code") String code, @RequestParam("val") String val) {
        return Strings.EMPTY;
    }
}

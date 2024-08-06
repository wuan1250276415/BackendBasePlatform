package pro.wuan.feignapi.dictapi.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.constant.AppConstant;
import pro.wuan.feignapi.dictapi.entity.SysDictionary;
import pro.wuan.feignapi.dictapi.fallback.DictFeignClientFallback;
import pro.wuan.feignapi.dictapi.model.DictParams;

import java.util.List;

/**
 * 数据字典feign接口
 *
 * @author: oldone
 * @date: 2021/9/8 10:53
 */
@FeignClient(
        value = AppConstant.APPLICATION_SYS_NAME,
        fallback = DictFeignClientFallback.class,
        path = "/feign/dict"
)
public interface DictFeignClient {

    /**
     * 根据相关参数获取字典项集合
     *
     * @param dictParams 字典查询参数实体
     * @return 字典项集合
     */
    @GetMapping(value = "/getListByParams",produces = { "application/json;charset=UTF-8"})
    List<SysDictionary> getListByParams(@RequestParam("dictParams") DictParams dictParams);

    /**
     * 根据父code获取字典项集合
     *
     * @param parentCode 父节点编码
     * @return 字典项集合
     */
    @GetMapping("/getListByParentCode")
    List<SysDictionary> getListByParentCode(@RequestParam("parentCode") String parentCode);


    /**
     * 根据字典code获取value值
     *
     * @param code 字典编码
     * @return
     */
    @GetMapping("/getDictItemValue")
    String getDictNameByCodeAndVal(@RequestParam("code") String code, @RequestParam("val") String val);
}

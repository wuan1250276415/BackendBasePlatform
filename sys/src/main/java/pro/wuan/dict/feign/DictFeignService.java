package pro.wuan.dict.feign;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.dict.service.ISysDictionaryService;
import pro.wuan.feignapi.dictapi.entity.SysDictionary;
import pro.wuan.feignapi.dictapi.model.DictParams;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典feign服务
 *
 * @author: oldone
 * @date: 2021/9/8 9:27
 */
@Slf4j
@RestController
@RequestMapping("/feign/dict")
public class DictFeignService {

    @Autowired
    private ISysDictionaryService dictionaryService;

    /**
     * 根据相关参数获取字典项集合
     *
     * @param dictParams 字典查询参数实体
     * @return 字典项集合
     */
    @GetMapping("/getListByParams")
    public List<SysDictionary> getListByParams(@RequestParam("dictParams") DictParams dictParams) {
        if (dictParams == null || dictParams.getParentId() == null) {
            return new ArrayList<>();
        }
        try {
            return dictionaryService.getByParentId(dictParams);
        } catch (Exception e) {
            log.error("dict getListByParentCode error:" + e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据父code获取字典项集合
     *
     * @param parentCode 父节点编码
     * @return 字典项集合
     */
    @GetMapping("/getListByParentCode")
    public List<SysDictionary> getListByParentCode(@RequestParam("parentCode") String parentCode) {
        if (Strings.isEmpty(parentCode)) {
            return new ArrayList<>();
        }
        try {
            return dictionaryService.bindDict(parentCode);
        } catch (Exception e) {
            log.error("dict getListByParentCode error:" + e);
            return new ArrayList<>();
        }
    }


    /**
     * 根据字典编码和字典项值获取字典名称
     *
     * @param code 字典编码
     * @param val  字典项值
     * @return
     */
    @GetMapping("/getDictItemValue")
    public String getDictNameByCodeAndVal(@RequestParam("code") String code, @RequestParam("val") String val) {
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(val)) {
            return Strings.EMPTY;
        }
        return dictionaryService.getDictNameByCodeAndVal(code, val);
    }


}

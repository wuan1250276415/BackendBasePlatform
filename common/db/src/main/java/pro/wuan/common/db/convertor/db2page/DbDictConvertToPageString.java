package pro.wuan.common.db.convertor.db2page;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pro.wuan.common.core.annotation.DicFiled;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.utils.ReflectionUtils;
import pro.wuan.common.db.service.IDbConvertToPageService;
import pro.wuan.feignapi.dictapi.entity.SysDictionary;
import pro.wuan.feignapi.dictapi.feign.DictFeignClient;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author chenzhong
 * @description 将数据字典的值转化为前台显示的内容
 * @date 2019年5月10日
 */
@Slf4j
@Service
public class DbDictConvertToPageString implements IDbConvertToPageService {
    @Lazy
    @Resource
    private DictFeignClient dictFeignClient;

    @Override
    public void execute(IDModel idModel) {
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), DicFiled.class);
        for (Field field : fields) {
            if (!idModel.hasConvert(field)) {
                continue;
            }
            log.debug("开始模块{}.{}的属性转换，将Dict转换成targetProperty", idModel.getClass().getName(), field.getName());
            //获取field的值
            executeField(idModel, field);
        }
    }

    @Override
    public void executeField(IDModel idModel, Field field) {
        if (idModel == null || field == null)
            return;
        if (field.isAnnotationPresent(DicFiled.class)) {
            Object fieldValue = ReflectionUtils.getFieldValue(idModel, field.getName());
            if (!StringUtils.isEmpty(fieldValue)) {
                DicFiled dicFiled = field.getAnnotation(DicFiled.class);
                ReflectionUtils.setFieldValue(idModel, dicFiled.targetProperty(), dictFeignClient.getDictNameByCodeAndVal(dicFiled.key(), String.valueOf(fieldValue)));
            }
        }
    }

    @Override
    public void executeListToPage(List<? extends IDModel> idModels) {
        if (CollectionUtils.isEmpty(idModels)) {
            return;
        }
        //获取第1个Entity 作为class
        IDModel idModel = idModels.get(0);
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), DicFiled.class);
        for (Field field : fields) {
            if (!idModel.hasConvert(field)) {
                continue;
            }
            DicFiled dicFiled = field.getDeclaredAnnotation(DicFiled.class);
            List<SysDictionary> dicts = dictFeignClient.getListByParentCode(dicFiled.key());
            for (IDModel model : idModels) {
                Object fieldValue = ReflectionUtils.getFieldValue(model, field.getName());
                SysDictionary dic = dicts.stream().filter(f -> f.getValue().equals(String.valueOf(fieldValue))).findFirst().orElse(null);
                if (dic != null) {
                    ReflectionUtils.setFieldValue(model, dicFiled.targetProperty(), dic.getName());
                }
            }
        }
    }
}

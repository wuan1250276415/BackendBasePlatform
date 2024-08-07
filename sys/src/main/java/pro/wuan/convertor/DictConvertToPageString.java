package pro.wuan.convertor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pro.wuan.common.core.annotation.DicFiled;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.utils.ReflectionUtils;
import pro.wuan.common.db.service.IDbConvertToPageService;
import pro.wuan.dict.service.ISysDictionaryService;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 将数据字典的值转化为前台显示的内容--sys模块专用
 *
 * @author: oldone
 * @date: 2021/9/9 9:43
 */
@Slf4j
@Service
public class DictConvertToPageString implements IDbConvertToPageService {
    @Lazy
    @Resource
    private ISysDictionaryService dictionaryService;

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
                ReflectionUtils.setFieldValue(idModel, dicFiled.targetProperty(), dictionaryService.getDictNameByCodeAndVal(dicFiled.key(), String.valueOf(fieldValue)));
            }
        }
    }

    @Override
    public void executeListToPage(List<? extends IDModel> idModels) {
        idModels.forEach(idModel -> {
            this.execute(idModel);
        });
    }
}

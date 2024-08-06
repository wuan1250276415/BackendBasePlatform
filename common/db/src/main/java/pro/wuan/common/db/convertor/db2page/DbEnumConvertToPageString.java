package pro.wuan.common.db.convertor.db2page;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.annotation.EnumFiled;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.utils.ReflectionUtils;
import pro.wuan.common.db.service.IDbConvertToPageService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @program: tellhowcloud
 * @description: 将enum字典的值转化为前台显示的内容
 * @author: HawkWang
 * @create: 2021-08-26 14:06
 **/
@Service
@Slf4j
public class DbEnumConvertToPageString implements IDbConvertToPageService {

    @Override
    public void execute(IDModel idModel) {
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), EnumFiled.class);
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
        try {
            if (idModel == null || field == null)
                return;
            if (field.isAnnotationPresent(EnumFiled.class)) {
                Object fieldValue = ReflectionUtils.getFieldValue(idModel, field.getName());
                if (ObjectUtils.anyNotNull(fieldValue)) {
                    EnumFiled enumFiled = field.getAnnotation(EnumFiled.class);
                    Class<Enum> clazz = (Class<Enum>) enumFiled.type();
                    //获取数据内容
                    Enum[] objs = clazz.getEnumConstants();
                    Method getCode = clazz.getMethod(enumFiled.codeMethod());
                    Method getName = clazz.getMethod(enumFiled.nameMethod());
                    for (Enum obj : objs) {
                        String code = getCode.invoke(obj).toString();
                        String name = getName.invoke(obj).toString();
                        if (code.equals(String.valueOf(fieldValue))) {
                            ReflectionUtils.setFieldValue(idModel, enumFiled.targetProperty(), name == null ? "" : name);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取枚举数据源的过程中出现异常，信息如下：", e.getMessage());
        }

    }

    @Override
    public void executeListToPage(List<? extends IDModel> idModels) {
        idModels.forEach(idModel -> {
            this.execute(idModel);
        });
    }

}

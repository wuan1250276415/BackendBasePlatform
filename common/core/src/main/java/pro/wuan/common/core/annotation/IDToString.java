package pro.wuan.common.core.annotation;


import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.service.IBaseFeignQuerySerivce;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IDToString {

    //需要转换的对象
    Class<? extends IDModel> type();

    ConvertMapping[] mappings() default {};

    /**
     * 非ID转STRING，使用uniqueKey去查询对象，uniqueKey必须可以唯一标识一个实体
     *
     * @return 唯一标识
     */
    String uniqueKey() default "";

    Class<? extends IBaseFeignQuerySerivce>[] feignService() default {};
}

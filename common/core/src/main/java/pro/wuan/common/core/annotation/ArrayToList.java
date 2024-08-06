package pro.wuan.common.core.annotation;


import pro.wuan.common.core.model.IDModel;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 数据库转页面：数组转List
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface ArrayToList {

    //需要转换的类型
    Class<? extends IDModel> type();

    //需要转换的属性
    String convertProperty();

    //目标属性
    String targetProperty();

}

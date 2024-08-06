package pro.wuan.common.core.annotation;


import pro.wuan.common.core.model.IDModel;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @description:
 **/
@Target(FIELD)
@Retention(RUNTIME)
public @interface ListToArray {

    /**
     * @description 关联表外键
     * @return
     */
    String fk();

    /**
     * @description 关联对象属性
     * @return
     */
    Class<? extends IDModel> fkType();

    //需要转换的属性
    String convertProperty();

    //目标属性
    String targetProperty();
}

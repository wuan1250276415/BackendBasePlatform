package pro.wuan.common.core.annotation;


import pro.wuan.common.core.model.IDModel;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @description:
 * @author: posiding
 * @create:2019-04-25 23:08
 **/
@Target(FIELD)
@Retention(RUNTIME)
public @interface ListToString {

    //外键
    String fk();

    //子表类型
    Class<? extends IDModel> fkType();

    //需要转换的类型
    Class<? extends IDModel> subType() default IDModel.class;

    String subFk();

    ConvertMapping[] mappings();
}

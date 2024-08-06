package pro.wuan.common.core.annotation.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-08 14:45
 **/
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyRadio {
    String defaultValue();

    PropertyOption[] options();

    String name();

}

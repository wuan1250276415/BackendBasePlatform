package pro.wuan.common.core.annotation.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-08 14:47
 **/
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyInteger {
    int defaultValue() default 0;

    String name();

    /**
     * 后续版本，不再限制大小
     * @return
     */
    @Deprecated
    int max() default 500;
}

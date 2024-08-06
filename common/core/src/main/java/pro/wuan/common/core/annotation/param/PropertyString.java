package pro.wuan.common.core.annotation.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-08 14:40
 **/
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyString {
    String name();

    /**
     * 后续版本，取消限制
     * @return
     */
    @Deprecated
    long max() default 200;

    /**
     * 后续版本，取消限制
     * @return
     */
    @Deprecated
    long min() default 1;

    String defaultValue();
}

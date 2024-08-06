package pro.wuan.common.core.annotation.param;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置成为系统参数
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-08s
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemProperty {
    String name();
}

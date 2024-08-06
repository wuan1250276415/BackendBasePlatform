package pro.wuan.common.core.annotation.sysenum;

import java.lang.annotation.*;

/**
 * 标识当前枚举可以对外访问
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-18 09:09
 **/
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EnumType {
    /**
     * 枚举的名字，全应用唯一
     *
     * @return
     */
    String name();
}

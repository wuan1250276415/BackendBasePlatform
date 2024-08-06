package pro.wuan.common.core.annotation;


import pro.wuan.common.core.service.IBaseService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 优先使用serviceName指定的服务，其次使用IBaseService的继承类
 */
@Target(value={ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceType {

    String serviceName() default "";

    Class<? extends IBaseService> service() default IBaseService.class;
    
}

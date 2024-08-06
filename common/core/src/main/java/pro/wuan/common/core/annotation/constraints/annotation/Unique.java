package pro.wuan.common.core.annotation.constraints.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pro.wuan.common.core.validator.UniqueValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 唯一性注解
 *
 * @author: oldone
 * @date: 2021/9/6 17:29
 */

@Documented
@Constraint(validatedBy = {UniqueValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface Unique {
    /**
     * 错误提示信息
     *
     * @return
     */
    String message() default "值已存在";

    /**
     * 前端远程访问的url
     * @return
     */
    String url() default "";

    /**
     * 联合校验的其他参数
     */
    String[] params() default {};

    /**
     * 验证服务接口
     * @return
     */
    String service();
    /**
     * 校验分组
     *
     * @return
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

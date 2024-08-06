package pro.wuan.common.core.annotation.constraints.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pro.wuan.common.core.validator.NotNullListValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * List集合不能为空和size必须>0校验
 *
 * @author: huangtianji
 * @date: 2021/11/25 21:43
 */

@Documented
@Constraint(validatedBy = {NotNullListValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface NotNullList {
    /**
     * 错误提示信息
     *
     * @return
     */
    String message() default "集合不能为空";

    /**
     * 校验分组
     *
     * @return
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

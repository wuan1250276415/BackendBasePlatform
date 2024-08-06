package pro.wuan.common.web.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import pro.wuan.common.web.validator.constraintvalidators.impl.LegalJSONValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 合法json校验
 *
 * @author robertHu
 * @date 2024/1/15
 */
@Documented
@Constraint(validatedBy = {LegalJSONValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@NotBlank(message = "json不能为空")
public @interface LegalJSON {

    boolean required() default true;

    String message() default "非法的json格式";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
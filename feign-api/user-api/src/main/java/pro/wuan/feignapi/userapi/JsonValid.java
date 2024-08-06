package pro.wuan.feignapi.userapi;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pro.wuan.feignapi.userapi.validator.JsonValidValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {JsonValidValidator.class})
public @interface JsonValid {

	String message() default "不是一个合法的JSON字符串";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}

package pro.wuan.common.web.validator;

import org.springframework.context.annotation.Import;
import pro.wuan.common.web.validator.config.ValidatorConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 激活FormValidator
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ValidatorConfiguration.class})
public @interface EnableFormValidator {
}

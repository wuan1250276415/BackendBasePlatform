package pro.wuan.common.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import pro.wuan.common.core.annotation.constraints.annotation.Unique;
import pro.wuan.common.core.utils.SpringUtil;

import java.lang.reflect.Method;

/**
 * 唯一性验证器
 *
 * @author: oldone
 * @date: 2021/9/6 17:36
 */
@Slf4j
public class UniqueValidator implements ConstraintValidator<Unique, Object> {
    private Unique parameters;

    @Override
    public void initialize(Unique parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        Object service = SpringUtil.getBean(parameters.service());
        try {
            Method method = service.getClass().getMethod("isExists", String.class);
            Boolean isExists = (Boolean) method.invoke(service, value);
            return !isExists;
        } catch (Exception e) {
            log.error("验证唯一性方法未找到");
            return false;
        }
    }
}

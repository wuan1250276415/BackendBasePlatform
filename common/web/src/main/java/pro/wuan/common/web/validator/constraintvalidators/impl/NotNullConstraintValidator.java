package pro.wuan.common.web.validator.constraintvalidators.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.internal.constraintvalidators.bv.NotNullValidator;
import pro.wuan.common.web.validator.constraintvalidators.IValidatable;


/**
 * 自定义一个验证 NotNull 的校验器。自定义类需要实现IValidatable接口
 *
 */
public class NotNullConstraintValidator implements ConstraintValidator<NotNull, IValidatable> {

    private final NotNullValidator notNullValidator = new NotNullValidator();

    @Override
    public void initialize(NotNull parameters) {
        notNullValidator.initialize(parameters);
    }

    @Override
    public boolean isValid(IValidatable value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && value.value() != null;
    }
}

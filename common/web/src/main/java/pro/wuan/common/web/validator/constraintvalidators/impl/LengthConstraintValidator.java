package pro.wuan.common.web.validator.constraintvalidators.impl;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.constraintvalidators.hv.LengthValidator;
import pro.wuan.common.web.validator.constraintvalidators.IValidatable;


/**
 * 自定义一个验证length的校验器。自定义类需要实现IValidatable接口
 *
 */
public class LengthConstraintValidator implements ConstraintValidator<Length, IValidatable> {

    private final LengthValidator lengthValidator = new LengthValidator();

    @Override
    public void initialize(Length parameters) {
        lengthValidator.initialize(parameters);
    }

    @Override
    public boolean isValid(IValidatable value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.value() == null) {
            return true;
        }
        return lengthValidator.isValid(String.valueOf(value.value()), constraintValidatorContext);
    }
}
